import React from "react";
import ReactDOM from "react-dom";
import { BrowserRouter, Route, Routes } from "react-router-dom";

import ClientSignup from "./auth/ClientSignup";
import ClientSignin from "./auth/ClientSignin";
import ClientProfile from "./ClientHomepage/ClientProfile";
import UpdateClientProfile from "./ClientHomepage/UpdateClientProfile";
import "./styling/styles.css";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<ClientSignin />}/>
                <Route path="/client_signup" element={<ClientSignup />} />
                <Route path="/client_signin" element={<ClientSignin />} />
                {/* <Route path="/client_homepage" element={<ClientHomepage />} /> */}
                <Route path="/client_profile" element={<ClientProfile />} /> 
                <Route path="/update_client_profile" element={<UpdateClientProfile />} />
            </Routes>
        </BrowserRouter>);
};

const rootElement = document.getElementById("root");
ReactDOM.render(<App />, rootElement);
