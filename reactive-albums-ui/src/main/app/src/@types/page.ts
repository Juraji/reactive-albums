export class Page<T> {
  public readonly totalPages: number;
  public readonly first: boolean;
  public readonly last: boolean;

  constructor(
    public readonly content: T[],
    public readonly currentPage: number,
    public readonly totalItems: number,
    public readonly size: number
  ) {
    this.totalPages = Math.ceil(totalItems / size);
    this.first = currentPage === 0;
    this.last = currentPage === this.totalPages - 1;
  }
}
