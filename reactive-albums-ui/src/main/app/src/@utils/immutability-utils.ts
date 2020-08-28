export function merge<T>(target: T | undefined, ...source: (T | undefined)[]): T {
  return Object.assign({}, target, ...source);
}

export function update<T, K extends keyof T = keyof T>(target: T | undefined, key: K, value: T[K]): T {
  return { ...target, [key]: value } as T;
}

export function remove<T>(target: T | undefined, key: keyof T): T {
  if (target === undefined) {
    return {} as T;
  }

  const { [key]: deleted, ...y } = target;
  return y as T;
}

export function append<T>(arr: T[] | undefined, ...items: T[]): T[] {
  return !!arr ? [...arr, ...items] : items;
}

export function replace<T>(arr: T[] | undefined, index: number, replacement: T): T[] {
  if (!!arr) {
    const copy = arr.slice();
    copy.splice(index, 1, replacement);
    return copy;
  } else {
    return [replacement];
  }
}

export function boundary(arr?: any[], endExclusive?: boolean) {
  if (!!arr) {
    return !!endExclusive ? arr.length - 1 : arr.length;
  } else {
    return !!endExclusive ? -1 : 0;
  }
}

export function isInBoundary(arr: any[] | undefined, index: number, startExclusive?: boolean, endExclusive?: boolean) {
  const innerBound = !!startExclusive ? 1 : 0;
  const outerBound = boundary(arr, !!endExclusive) - 1;
  return index >= innerBound && index <= outerBound;
}

export function isEmpty(countable?: { length: number }): boolean {
  return !countable || countable.length === 0;
}

export function values<K extends string, V, T extends Record<K, V | undefined>>(obj: T | undefined): V[] {
  return (!!obj ? Object.values(obj).filter((v) => !!v) : []) as V[];
}
