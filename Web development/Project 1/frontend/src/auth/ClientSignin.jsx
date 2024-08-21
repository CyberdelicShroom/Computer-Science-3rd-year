import React, { useState } from "react";
import { Formik, Field, Form } from "formik";
import Layout from "./Layout";
import * as Yup from 'yup';
import { useNavigate } from "react-router-dom";


const ClientSignin = () => {
    return <Layout><SigninForm /></Layout>;
};

const SigninForm = () => {
    const [login_info, setSignIn_info] = useState();
    let navigate = useNavigate();
    async function getClientSignInInfo(values){
        const options = { 
            method: 'POST',
            headers: {
                'Content-Type': 'application/json;charset=utf-8' 
            }, 
            body: JSON.stringify(values)};
        const response = await fetch("/devSignin", options);
        const data = await response.json();
        
        if (data.login_status){
            console.log(data);
            navigate("/dev_homepage");
        } else {
            setSignIn_info(data.details);
            console.error(data);
        }
    }
    const DisplayingErrorMessagesSchema = Yup.object().shape({
        email: Yup.string().email("Email must be valid").required('Email is required'),
        password: Yup.string().required('Password is required'),
    });
    return (
        
        <Formik
            initialValues={{ email: "", password: "" }}
            validationSchema={DisplayingErrorMessagesSchema}
            onSubmit={(values) => {
                getClientSignInInfo(values);
                //alert(JSON.stringify(values, null, 2));
            } }
        >{props => (<Form style={{ display: "grid", justifyContent: "center", gap: "0.9rem" }}>
            {/*When password is incorrect or user does not exist, this login_info p-tag only displays
            the second time you click login. It needs to display after the first click.*/}
            <Field name="email" placeholder="Email" />
            {props.touched.email && props.errors.email && <div>{props.errors.email}</div>}
            <Field name="password" type="password" placeholder="Password" />
            {props.touched.password && props.errors.password && <div>{props.errors.password}</div>}
            <button type="submit">Sign In</button>
            <button type="button" onClick={() => navigate("/client_signup")}>Not Registered? Click here</button>
            <p style={{color: "rosybrown", textAlign: "center"}}>{login_info}</p>
        </Form>)}
        </Formik>
    );
};

export default ClientSignin;
