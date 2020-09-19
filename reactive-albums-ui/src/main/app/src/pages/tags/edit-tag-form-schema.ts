import { object, string } from 'yup';

export interface EditTagFormValues {
  label: string;
  tagColor?: string;
  textColor?: string;
}

export const editTagFormSchema = object<EditTagFormValues>({
  label: string().max(255).required(),
  tagColor: string().matches(/#[0-9a-f]{6}/),
  textColor: string().matches(/#[0-9a-f]{6}/),
});
