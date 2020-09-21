import { Audited } from './api';

export interface Directory extends Audited {
  id: string;
  location: string;
  displayName: string;
  automaticScanEnabled: boolean;
}
