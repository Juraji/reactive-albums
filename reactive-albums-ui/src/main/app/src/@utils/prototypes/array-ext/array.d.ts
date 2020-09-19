interface Array<T> {
  replace(this: Array<T>, index: number, replacement: T): Array<T>;

  withinBounds(this: Array<T>, index: number, startExclusive?: boolean, endExclusive?: boolean): boolean;

  isEmpty(this: Array<T>): boolean;

  isNotEmpty(this: Array<T>): boolean;

  filterNotNull<U = T extends undefined | null ? never : T>(this: Array<T>): U[];

  partition<K extends number | string | symbol>(this: Array<T>, partitionBy: (t: T) => K): Record<K, T[]>;
}
