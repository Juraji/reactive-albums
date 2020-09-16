export class PageDeprecated<T> {
  public readonly totalPages: number;
  public readonly first: boolean;
  public readonly last: boolean;

  constructor(
    public readonly content: T[] = [],
    public readonly currentPage: number = 0,
    public readonly totalItems: number = 0,
    public readonly size: number = 0,
    public readonly filter: string
  ) {
    this.totalPages = Math.ceil(totalItems / size);
    this.first = currentPage === 0;
    this.last = currentPage === this.totalPages - 1;
  }
}
