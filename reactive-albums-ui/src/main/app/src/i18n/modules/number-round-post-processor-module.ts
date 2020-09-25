import { FnPostProcessorModule } from './fn-post-processor-module.abstract';

export class RoundPostProcessorModule extends FnPostProcessorModule {
  constructor() {
    super('round');
  }

  run(number: string, precision: string): string {
    return parseInt(number).toFixed(parseInt(precision) || 0);
  }
}
