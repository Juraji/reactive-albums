/**
 * Number range generator
 *
 * @param start The start index (inclusive)
 * @param end The end index (exclusive)
 */
export function* range(start: number, end: number): Iterable<number> {
  let i = start;
  while (i < end) {
    yield i++;
  }
}

export function rangeAsArray(start: number, end: number): number[] {
  return Array.from(range(start, end));
}
