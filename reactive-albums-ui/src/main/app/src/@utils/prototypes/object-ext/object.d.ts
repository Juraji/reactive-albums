interface Object {
  copy<T>(this: T, update?: T extends Array<any> ? never : Partial<T>): T;
  copyNotNull<T>(this: T, update?: T extends Array<any> ? never : Partial<T>): T;
  merge<T, U>(this: T, other: U): T & U;
}
