import { FieldMetaProps, FormikProps } from 'formik';
import { formikIsFormValid, formikIsInvalid, formikIsValid } from '@utils';
import installExtensions from './prototypes';

installExtensions();

interface TestForm {
  field1: string;
}

function createFormState(props?: Partial<FormikProps<TestForm>>): FormikProps<TestForm> {
  const state = Object.assign(
    {
      dirty: true,
      errors: {},
      initialErrors: {},
      initialTouched: {},
      initialValues: { field1: '' },
      isSubmitting: false,
      isValid: true,
      isValidating: false,
      submitCount: 0,
      touched: {},
      values: { field1: '' },
    },
    props
  ) as FormikProps<TestForm>;

  state.getFieldMeta = (name: string): FieldMetaProps<any> => {
    return {
      initialTouched: false,
      // @ts-ignore
      initialValue: state.values[name],
      // @ts-ignore
      touched: state.touched[name],
      // @ts-ignore
      value: state.values[name],
      // @ts-ignore
      error: state.errors[name],
    };
  };

  return state;
}

describe('formik-utils', () => {
  describe('#formikIsValid', () => {
    it('should return false on field key untouched', () => {
      const formProps = createFormState();
      expect(formikIsValid(formProps, 'field1')).toBeFalsy();
    });

    it('should return false on field key touched and in error', () => {
      const formProps = createFormState({ touched: { field1: true }, errors: { field1: 'Error' } });
      expect(formikIsValid(formProps, 'field1')).toBeFalsy();
    });

    it('should return true on field key touched and valid', () => {
      const formProps = createFormState({ touched: { field1: true } });
      expect(formikIsValid(formProps, 'field1')).toBeTruthy();
    });
  });

  describe('#formikIsInvalid', () => {
    it('should return false on field key untouched', () => {
      const formProps = createFormState();
      expect(formikIsInvalid(formProps, 'field1')).toBeFalsy();
    });

    it('should return true on field key touched and in error', () => {
      const formProps = createFormState({ touched: { field1: true }, errors: { field1: 'Error' } });
      expect(formikIsInvalid(formProps, 'field1')).toBeTruthy();
    });
  });

  describe('#formikIsFormValid', () => {
    it('should return false on untouched', () => {
      const formProps = createFormState({ isValid: true });
      expect(formikIsFormValid(formProps)).toBeFalsy();
    });

    it('should return false on touched, but invalid state', () => {
      const formProps = createFormState({ isValid: false, touched: { field1: true } });
      expect(formikIsFormValid(formProps)).toBeFalsy();
    });

    it('should return false on touched, but in error', () => {
      const formProps = createFormState({ isValid: true, touched: { field1: true }, errors: { field1: 'Error' } });
      expect(formikIsFormValid(formProps)).toBeFalsy();
    });

    it('should return true on touched and without errors', () => {
      const formProps = createFormState({ isValid: true, touched: { field1: true } });
      expect(formikIsFormValid(formProps)).toBeTruthy();
    });

    it('should respect extra predicate', () => {
      const formProps = createFormState({ isValid: true, touched: { field1: true } });

      expect(formikIsFormValid(formProps, () => false)).toBeFalsy();
      expect(formikIsFormValid(formProps, () => true)).toBeTruthy();
    });
  });
});
