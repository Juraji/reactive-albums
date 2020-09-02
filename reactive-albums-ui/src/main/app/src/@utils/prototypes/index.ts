import installArrayExt from './array-ext';
import installObjExt from './object-ext';

export default function installExtensions() {
  installArrayExt();
  installObjExt();
}
