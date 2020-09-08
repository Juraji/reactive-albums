interface Object {
  copy<T>(this: T, update?: T extends Array<any> ? never : Partial<T>): T;
}
