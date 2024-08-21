import React, { useState } from "react";
import { Formik, Field, Form } from "formik";
import Layout from "./Layout";
import * as Yup from 'yup';
import { useNavigate } from "react-router-dom";
import Axios from 'axios';

const ClientSignin = () => {
    return <div>

        <Layout><SigninForm /></Layout>

    </div>;
};

const SigninForm = () => {
    const [login_info, setSignIn_info] = useState();
    let navigate = useNavigate();
    
    async function getClientSignInInfo(values) {

        Axios.post('api/user/login', {
            email: values.email,
            password: values.password,
        })
        .then((res) => {
            if(res.data.status !== "success"){
                setSignIn_info(res.data.details);
            } else {
                console.log(res.data);
                navigate("/client_homepage");
            }
        })
    }
    const DisplayingErrorMessagesSchema = Yup.object().shape({
        email: Yup.string().email("Email must be valid").required('Email is required'),
        password: Yup.string().required('Password is required'),
    });
    return (
        <><h2 style={{textAlign: "center", color: "steelblue", textDecorationLine: "underline"}}>Welcome to Social Connect</h2>
        <Formik
            initialValues={{ email: "", password: "" }}
            validationSchema={DisplayingErrorMessagesSchema}
            onSubmit={(values) => {
                getClientSignInInfo(values);
            } }
        >{props => (<Form style={{ display: "grid", justifyContent: "center", gap: "0.9rem" }}>
            <Field name="email" placeholder="Email" />
            {props.touched.email && props.errors.email && <div>{props.errors.email}</div>}
            <Field name="password" type="password" placeholder="Password" />
            {props.touched.password && props.errors.password && <div>{props.errors.password}</div>}
            <button type="submit">Sign In</button>
            <button onClick={() => navigate("/client_signup")}>Not Registered? Click here</button>
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr" }}>
                <label>< onclick Field type="checkbox" name="RememberMe" value="RememberMe" />Remember Me</label>
            </div>
            <p style={{ color: "rosybrown", textAlign: "center" }}>{login_info}</p>

        </Form>)}
        </Formik></>
    );
};

export default ClientSignin;
