/**
 * Node 25 内置了 localStorage 全局对象，它会遮蔽 jsdom 的实现，
 * 且未通过 --localstorage-file 指定路径时调用即抛错。
 *
 * 这里装一个内存版实现。必须作为独立的 setupFile 排在 setup.ts 之前：
 * setup.ts 顶层 import 了 authStore，而 authStore 在模块初始化时就会读 localStorage，
 * ES import 会被提升，写在 setup.ts 内部就太晚了。
 */
class LocalStorageMock implements Storage {
  private store = new Map<string, string>()

  get length() {
    return this.store.size
  }

  clear() {
    this.store.clear()
  }

  getItem(key: string) {
    return this.store.get(key) ?? null
  }

  key(index: number) {
    return Array.from(this.store.keys())[index] ?? null
  }

  removeItem(key: string) {
    this.store.delete(key)
  }

  setItem(key: string, value: string) {
    this.store.set(key, String(value))
  }
}

for (const target of [globalThis, window]) {
  Object.defineProperty(target, 'localStorage', {
    configurable: true,
    writable: true,
    value: new LocalStorageMock(),
  })
}
