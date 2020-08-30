export class Arrays {
  static append<T>(arr: T[] | undefined, ...items: T[]): T[] {
    return !!arr ? [...arr, ...items] : items;
  }

  static replace<T>(arr: T[] | undefined, index: number, replacement: T): T[] {
    if (!!arr) {
      const copy = arr.slice();
      copy.splice(index, 1, replacement);
      return copy;
    } else {
      return [replacement];
    }
  }

  static update<T, K extends keyof T = keyof T>(arr: T[], index: number, key: K, value: T[K]): T[] {
    if (!!arr && index in arr) {
      const o = arr[index];
      return this.replace(arr, index, Objects.update(o, key, value));
    } else {
      throw Error('No value to update!');
    }
  }

  static boundary(arr?: any[], endExclusive?: boolean): number {
    if (!!arr) {
      return !!endExclusive ? arr.length - 1 : arr.length;
    } else {
      return !!endExclusive ? -1 : 0;
    }
  }

  static isInBoundary(
    arr: any[] | undefined,
    index: number,
    startExclusive?: boolean,
    endExclusive?: boolean
  ): boolean {
    const innerBound = !!startExclusive ? 1 : 0;
    const outerBound = this.boundary(arr, !!endExclusive) - 1;
    return index >= innerBound && index <= outerBound;
  }

  static remove<T>(arr: T[] | undefined, index: number): T[] {
    if (!!arr && index in arr) {
      return arr.splice(index, 1);
    } else {
      return arr || [];
    }
  }

  static insert<T>(arr: T[] | undefined, index: number, ...items: T[]) {
    if (!!arr) {
      return [...arr.slice(0, index), ...items, ...arr.slice(index)];
    } else {
      return items;
    }
  }
}

export class Objects {
  static update<T, K extends keyof T = keyof T>(target: T | undefined, key: K, value: T[K]): T {
    return { ...target, [key]: value } as T;
  }

  static merge<T>(target: T | undefined, ...source: (Partial<T> | undefined)[]): T {
    return Object.assign({}, target, ...source);
  }

  static unset<T>(target: T | undefined, key: keyof T): T {
    if (target === undefined) {
      return {} as T;
    }

    const { [key]: deleted, ...y } = target;
    return y as T;
  }

  static values<K extends string, V, T extends Record<K, V | undefined>>(obj: T | undefined): V[] {
    return (!!obj ? Object.values(obj).filter((v) => !!v) : []) as V[];
  }
}

export function isEmpty(countable?: { length: number }): boolean {
  return !countable || countable.length === 0;
}
