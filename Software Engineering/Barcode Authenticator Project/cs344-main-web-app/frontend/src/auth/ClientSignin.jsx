import React, { useState, useEffect } from "react";
import { Formik, Field, Form } from "formik";
import Layout from "./Layout";
import * as Yup from 'yup';
import { useNavigate } from "react-router-dom";
import Axios from 'axios';
import { Container } from "@mui/material";
import { Box } from "@mui/material";
import QRCode from "react-qr-code";

const ClientSignin = () => {
    return <div>

        <Layout><SigninForm /></Layout>

    </div>;
};

const SigninForm = () => {
    const [login_info, setSignIn_info] = useState();
    let navigate = useNavigate();
    const [QR="", setQRCode] = useState();
    const [intervalId, setIntervalCheck] = useState();

    useEffect(() => {
        getQR();
    }, [])

    useEffect(() => {
        setIntervalCheck(setInterval(() => {
            Axios.post('api/user/checklogin').then((res) => {
                if(res.data.status !== "success") {
                    console.log("unsuccessful")
                } else {
                    console.log("success")
                    navigate("/client_profile");
                    return () => clearInterval(this);
                }
            });
        }, 2000));
      }, []);

    async function getQR(){
        Axios.get('/getqr').then((res) => {
            if(res.data.status !== "success") {
                console.log("unsuccessful")
            } else {
                let QRcode = res.data.QR;
                console.log("QR Code: " + QRcode);
                setQRCode(QRcode);
            }
        })
    }

    async function register() {
        clearInterval(intervalId);
        navigate("/client_signup");
    }

    async function getClientSignInInfo(values) {

        Axios.post('api/user/login', {
            email: values.email,
            password: values.password,
        })
            .then((res) => {
                if (res.data.status !== "success") {
                    setSignIn_info(res.data.details);
                } else {
                    console.log(res.data);
                    clearInterval(intervalId);
                    navigate("/client_profile");
                }
            })
    }
    const DisplayingErrorMessagesSchema = Yup.object().shape({
        email: Yup.string().email("Email must be valid").required('Email is required'),
        password: Yup.string().required('Password is required'),
    });
    return (
        <><h2 style={{ textAlign: "center", color: "steelblue", textDecorationLine: "underline" }}>Barcode Web App</h2>
            <Formik
                initialValues={{ email: "", password: "" }}
                validationSchema={DisplayingErrorMessagesSchema}
                onSubmit={(values) => {
                    getClientSignInInfo(values);
                }}
            >{props => (<Form style={{ display: "grid", justifyContent: "center", gap: "0.9rem" }}>
                <Field name="email" placeholder="Email" />
                {props.touched.email && props.errors.email && <div>{props.errors.email}</div>}
                <Field name="password" type="password" placeholder="Password" />
                {props.touched.password && props.errors.password && <div>{props.errors.password}</div>}
                <button type="submit">Sign In</button>
                <p align="center">OR</p>
                <Container align="center">
                    <QRCode value={QR}/>
                </Container>

                <button onClick={() => register()}>Not Registered? Click here</button>

                <p style={{ color: "rosybrown", textAlign: "center" }}>{login_info}</p>

            </Form>)}
            </Formik></>
    );
};

export default ClientSignin;
