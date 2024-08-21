import React from "react";
import { Formik, Field, Form } from "formik";
import Layout from "../auth/Layout";
import * as Yup from 'yup';

const CreateCompanyContracts = () => {
  return <Layout><SigninForm /></Layout>;
};

const SigninForm = () => {
  const DisplayingErrorMessagesSchema = Yup.object().shape({
    length: Yup.string().required('Contract length is required'),
    value: Yup.string().required('Contract value is required'),
    language: Yup.string().required('Preferred language is required'),
    description: Yup.string().required('Contract description is required'),
  });
  return (
    <><Formik
      initialValues={{ length: "", value: "", description: "", language: "", remote: false, office: false }}
      validationSchema={DisplayingErrorMessagesSchema}
      onSubmit={(values) => {
        fetch("dummy url for now", { method: 'POST', headers: { 'Content-Type': 'application/json;charset=utf-8' }, body: JSON.stringify(values) });
        alert(JSON.stringify(values, null, 2));
      }}
    >{props => (<Form style={{ display: "grid", justifyContent: "center", gap: "0.9rem" }}>
      <Field name="length" placeholder="Contract length (Months)" />
      {props.touched.length && props.errors.length && <div>{props.errors.length}</div>}
      <Field name="value" placeholder="Contract value (ZAR)" />
      {props.touched.value && props.errors.value && <div>{props.errors.value}</div>}
      {/* <label style={{ marginBottom: "0.6rem" }}>Please enter the preferred languages:</label> */}
      
      <div>
        <label style={{ marginBottom: "1rem", marginTop: "1rem" }}>Please enter the preferred languages:</label>
        <textarea
          id="language"
          name="language"
          onChange={props.handleChange}
          value={props.values.language} />
      </div>

      <label style={{ marginBottom: "0.6rem" }}>Please select contract location:</label>
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr" }}>
        <div><input type="checkbox" name="remote" value={props.values.remote} onChange={props.handleChange} />Remote</div>
        <div><input type="checkbox" name="office" value={props.values.office} onChange={props.handleChange} />In office</div>
      </div>
      <div>
        <label style={{ marginBottom: "1rem", marginTop: "1rem" }}>Enter contract description:</label>
        <textarea
          id="workExperience"
          name="workExperience"
          onChange={props.handleChange}
          value={props.values.workExperience} />
      </div>
      <button type="submit">Create</button>
    </Form>)}
    </Formik></>
  );
};

export default CreateCompanyContracts;