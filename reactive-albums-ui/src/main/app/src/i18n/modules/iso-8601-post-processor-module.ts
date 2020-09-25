import { FnPostProcessorModule } from './fn-post-processor-module.abstract';
import { format as formatDate, parseISO } from 'date-fns';

export class Iso8601DateFnsFmtModule extends FnPostProcessorModule {
  constructor() {
    super('iso8601Fmt');
  }

  run(value: string, fmt: string): string {
    const dt = parseISO(value);
    if (isNaN(dt.getTime())) {
      console.error(`Unable to parse "${value}" as date using "${fmt}"`);
      return value;
    } else {
      return formatDate(dt, fmt);
    }
  }
}
