import { boolean, object, string } from 'yup';
import { validatePath } from '@utils';

export interface RegisterDirectoryForm {
  location: string;
  recursive: boolean;
}

export const registerDirectoryFormInitialValues: RegisterDirectoryForm = {
  location: '',
  recursive: true,
};

export const registerDirectoryFormSchema = object<RegisterDirectoryForm>({
  location: string().test('invalidPath', 'Location must be a valid path', validatePath).required(),
  recursive: boolean().required(),
});
