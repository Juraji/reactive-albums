export const isWindows = navigator.platform.includes('Win');

export function isEnv(env: 'development' | 'production' | 'test'): boolean {
  return process.env.NODE_ENV === env;
}

export function isDevelopmentEnv(): boolean {
  return isEnv('development');
}

export function isTestEnv(): boolean {
  return isEnv('test');
}

export function isProductionEnv(): boolean {
  return isEnv('production');
}

export function validatePath(path: string): boolean {
  if (isWindows) {
    const winPathRegx = /^[a-z]:[/\\](?:[./\\ ](?![./\\\n])|[^<>:"|?*!./\\ \n])*$/i;
    return winPathRegx.test(path);
  } else {
    const unixPathRegx = /^\/[^\0]*$/;
    return unixPathRegx.test(path);
  }
}
