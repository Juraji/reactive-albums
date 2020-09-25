import fileSize from 'filesize';
import { FnPostProcessorModule } from './fn-post-processor-module.abstract';

export class FileSizePostProcessorModule extends FnPostProcessorModule {
  constructor() {
    super('fileSize');
  }

  run(value: string): string {
    return fileSize(parseInt(value));
  }
}
