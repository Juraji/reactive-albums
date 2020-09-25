import { i18n, PostProcessorModule, ThirdPartyModule, TOptions } from 'i18next';

export class PostProcessorModuleChain implements ThirdPartyModule {
  type = '3rdParty' as any;

  private constructor(private modules: PostProcessorModule[]) {}

  static chainOf(...modules: (new () => PostProcessorModule)[]): PostProcessorModuleChain {
    return new PostProcessorModuleChain(modules.map((mc) => new mc()));
  }

  init(i18next: i18n): void {
    this.modules.forEach((m) => i18next.use(m));
  }

  postProcessorNames(): string[] {
    return this.modules.map((m) => m.name);
  }
}

export abstract class FnPostProcessorModule implements PostProcessorModule {
  private filter = `${this.fnName}(`;
  private fnRegexp = new RegExp(`${this.fnName}\\(([^)]+)\\)`, 'g');
  type = 'postProcessor' as any;
  name = `${this.fnName}FnPostProcessor`;

  protected constructor(private fnName: string) {}

  process(value: string, key: string, options: TOptions, translator: any): string {
    if (value.includes(this.filter)) {
      return value.replaceAll(this.fnRegexp, (substring, fnArgs: string) => {
        const argsSplit = fnArgs.split(',').map((x) => x.trim());
        return this.run(...argsSplit);
      });
    } else {
      return value;
    }
  }

  abstract run(...args: string[]): string;
}
