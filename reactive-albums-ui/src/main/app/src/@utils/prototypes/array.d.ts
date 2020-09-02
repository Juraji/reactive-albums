interface Array<T> {
  replace(this: Array<T>, index: number, replacement: T): Array<T>;

  withinBounds(this: Array<T>, index: number, startExclusive?: boolean, endExclusive?: boolean): boolean;

  isEmpty(this: Array<T>): boolean;
}
