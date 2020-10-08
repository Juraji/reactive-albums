import { FnPostProcessorModule } from './fn-post-processor-module.abstract';
import { format as formatDate, parseISO } from 'date-fns';

export class Iso8601DateFnsFmtModule extends FnPostProcessorModule {
  constructor() {
    super('iso8601Fmt');
  }

  run(value: string, fmt: string, defaultValue?: string): string {
    const dt = parseISO(value);
    if (isNaN(dt.getTime())) {
      return defaultValue || value;
    } else {
      return formatDate(dt, fmt);
    }
  }
}
