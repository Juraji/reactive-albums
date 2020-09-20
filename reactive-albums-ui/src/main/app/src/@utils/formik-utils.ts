import { FormikComputedProps, FormikProps, FormikState } from 'formik';
import { ChangeEvent, FocusEvent } from 'react';

interface BSFormikControlProps {
  name: string;
  onChange: (e: ChangeEvent<any>) => void;
  onFocus: (e: FocusEvent<any>) => void;
  isValid: boolean;
  isInvalid: boolean;
}

export function formikIsValid<T>(formikBag: FormikProps<T>, key: keyof T | string): boolean {
  const fieldMetaProps = formikBag.getFieldMeta(key as string);
  return fieldMetaProps.touched && !fieldMetaProps.error;
}

export function formikIsInvalid<T>(formikBag: FormikProps<T>, key: keyof T | string): boolean {
  const fieldMetaProps = formikBag.getFieldMeta(key as string);
  return !!fieldMetaProps.error;
}

export function formikControlProps<T>(formikBag: FormikProps<T>, key: keyof T | string): BSFormikControlProps {
  return {
    name: key as string,
    onChange: formikBag.handleChange,
    onFocus: formikBag.handleBlur,
    isValid: formikIsValid(formikBag, key),
    isInvalid: formikIsInvalid(formikBag, key),
  };
}

export function formikIsFormValid<T>(
  formikProps: FormikState<T> & FormikComputedProps<T>,
  extraPredicate?: () => boolean
): boolean {
  return (
    formikProps.isValid &&
    Object.keys(formikProps.touched).isNotEmpty() &&
    Object.keys(formikProps.errors).isEmpty() &&
    (!extraPredicate || extraPredicate())
  );
}
