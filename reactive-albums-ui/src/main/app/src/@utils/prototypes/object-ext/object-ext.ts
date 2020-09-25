/* eslint-disable */
///<reference path="object.d.ts"/>

export default function () {
  Object.defineProperty(Object.prototype, 'copy', {
    configurable: false,
    enumerable: false,
    writable: false,
    value: function <T>(this: T, update?: Partial<T>): T {
      if (Array.isArray(this)) {
        if (!!update) {
          throw Error('Can not apply update to arrays');
        }

        // @ts-ignore
        return this.slice();
      } else {
        return { ...this, ...update };
      }
    },
  });

  Object.defineProperty(Object.prototype, 'copyNotNull', {
    configurable: false,
    enumerable: false,
    writable: false,
    value: function <T, K extends keyof T>(this: T, update?: Partial<T>): T {
      if (Array.isArray(this)) {
        if (!!update) {
          throw Error('Can not apply update to arrays');
        }

        // @ts-ignore
        return this.slice();
      } else if (!!update) {
        const nnUpdate = update.copy();

        for (const k in nnUpdate) {
          if (nnUpdate.hasOwnProperty(k) && (nnUpdate[k] === null || nnUpdate[k] === undefined)) {
            delete nnUpdate[k];
          }
        }

        // @ts-ignore
        return this.copy(nnUpdate);
      } else {
        // @ts-ignore
        return this.copy();
      }
    },
  });

  Object.defineProperty(Object.prototype, 'merge', {
    configurable: false,
    enumerable: false,
    writable: false,
    value: function merge<T, U>(this: T, other: U): T & U {
      if (Array.isArray(this)) {
        // @ts-ignore
        return this.concat(other);
      } else {
        return { ...this, ...other };
      }
    },
  });
}
