import { useQuery } from '@tanstack/react-query'
import { Button, Card, Form, Select, Table, Typography } from 'antd'
import type { ChangeEvent } from 'react'
import { useState } from 'react'
import { httpClient, type PageResponse } from '../../api/http'
import { TOKEN_STORAGE_KEY } from '../auth/authStore'

type CategoryItem = {
  id: number
  name: string
  parentId: number | null
}

type ImportPreviewItem = {
  title: string
  url: string
  folderPath: string
}

type ImportPreviewResponse = {
  fileName: string
  items: ImportPreviewItem[]
}

type ImportResultResponse = {
  totalCount: number
  successCount: number
  failedCount: number
}

function ImportPage() {
  const [preview, setPreview] = useState<ImportPreviewResponse | null>(null)
  const [result, setResult] = useState<ImportResultResponse | null>(null)
  const [selectedCategoryId, setSelectedCategoryId] = useState<number | undefined>()
  const [parsing, setParsing] = useState(false)
  const [confirming, setConfirming] = useState(false)

  const { data: categories } = useQuery({
    queryKey: ['import-categories'],
    queryFn: async () => {
      const response = await httpClient.get<PageResponse<CategoryItem>>('/categories')
      return response.data.items
    },
  })

  async function handleFileChange(event: ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0]
    if (!file) {
      return
    }

    const formData = new FormData()
    formData.append('file', file)
    setParsing(true)
    try {
      const token = window.localStorage.getItem(TOKEN_STORAGE_KEY)
      const response = await fetch('/api/imports/parse', {
        method: 'POST',
        body: formData,
        headers: token ? { Authorization: `Bearer ${token}` } : undefined,
      })
      const data = (await response.json()) as ImportPreviewResponse
      setPreview(data)
      setResult(null)
    } finally {
      setParsing(false)
      event.target.value = ''
    }
  }

  async function handleConfirm() {
    if (!preview || selectedCategoryId == null) {
      return
    }

    setConfirming(true)
    try {
      const response = await httpClient.post<ImportResultResponse>('/imports/confirm', {
        fileName: preview.fileName,
        categoryId: selectedCategoryId,
        items: preview.items,
      })
      setResult(response.data)
    } finally {
      setConfirming(false)
    }
  }

  return (
    <Card>
      <Typography.Title level={3}>书签导入</Typography.Title>
      <Typography.Paragraph type="secondary">
        上传浏览器导出的书签 HTML，预览后再确认导入。
      </Typography.Paragraph>
      <Form layout="vertical">
        <Form.Item label="上传书签文件">
          <input id="import-file" aria-label="上传书签文件" type="file" accept=".html,text/html" onChange={handleFileChange} />
        </Form.Item>
        <Form.Item label="导入目标分类">
          <Select
            allowClear
            placeholder="请选择分类"
            value={selectedCategoryId}
            onChange={setSelectedCategoryId}
            options={(categories ?? []).map((category) => ({
              label: category.name,
              value: category.id,
            }))}
          />
        </Form.Item>
        <Button type="primary" onClick={() => void handleConfirm()} disabled={!preview || selectedCategoryId == null} loading={confirming}>
          确认导入
        </Button>
      </Form>
      <Table<ImportPreviewItem>
        style={{ marginTop: 24 }}
        rowKey={(item) => `${item.url}-${item.folderPath}`}
        loading={parsing}
        pagination={false}
        dataSource={preview?.items ?? []}
        columns={[
          { title: '标题', dataIndex: 'title' },
          { title: 'URL', dataIndex: 'url' },
          { title: '文件夹', dataIndex: 'folderPath' },
        ]}
      />
      {result ? (
        <Card style={{ marginTop: 24 }}>
          <Typography.Text>总条数：{result.totalCount}</Typography.Text>
          <br />
          <Typography.Text>成功条数：{result.successCount}</Typography.Text>
          <br />
          <Typography.Text>失败条数：{result.failedCount}</Typography.Text>
        </Card>
      ) : null}
    </Card>
  )
}

export default ImportPage
