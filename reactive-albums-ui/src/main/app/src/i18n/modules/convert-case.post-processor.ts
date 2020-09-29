import { FnPostProcessorModule } from './fn-post-processor-module.abstract';

export class ConvertCasePostProcessorModule extends FnPostProcessorModule {
  constructor() {
    super('convertCase');
  }

  run(value: string, caseType: string): string {
    switch (caseType) {
      case 'LOWER':
        return value.toLowerCase();
      case 'UPPER':
        return value.toLowerCase();
      default:
        return value;
    }
  }
}
