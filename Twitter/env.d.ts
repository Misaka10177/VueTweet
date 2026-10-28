/// <reference types="vite/client" />
declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<Record<string, never>, Record<string, never>, unknown>
  export default component
}

interface ImportMetaEnv {
  readonly VITE_APP_TITLE?: string
  // 根据需要添加其他环境变量定义
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
