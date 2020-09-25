import { FnPostProcessorModule } from './fn-post-processor-module.abstract';

export class IfElsePostProcessorModule extends FnPostProcessorModule {
  constructor() {
    super('ifElse');
  }

  run(condition: string, ifValue: string, elseValue: string = ''): string {
    return condition === 'true' ? ifValue : elseValue;
  }
}
