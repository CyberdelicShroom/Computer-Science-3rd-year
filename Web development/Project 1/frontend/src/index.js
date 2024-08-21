import React from "react";
import ReactDOM from "react-dom";
import { BrowserRouter, Route, Routes } from "react-router-dom";

import ClientSignup from "./auth/ClientSignup";
import ClientSignin from "./auth/ClientSignin";
import CompanySignIn from "./auth/CompanySignin";
import CompanyRegister from "./auth/CompanySignup";
import Landingpage from "./Landingpage";
import DevHomepage from "./devHomepage/DevHomepage";
import DevProfile from "./devHomepage/DevProfile";
import UpdateDevProfile from "./devHomepage/ViewProfile";
import DevContracts from "./devHomepage/DevContracts";
import CreateCompanyContracts from "./companyHomepage/CreateCompanyContracts";
import CompanyProfile from "./companyHomepage/CompanyProfile";
import UpdateCompanyProfile from "./companyHomepage/ViewProfile";

import "./styling/styles.css";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Landingpage />} />

                <Route path="/client_signup" element={<ClientSignup />} />
                <Route path="/client_signin" element={<ClientSignin />} />
                <Route path="/dev_homepage" element={<DevHomepage />} />
                <Route path="/dev_contracts" element={<DevContracts />} />
                <Route path="/dev_profile" element={<DevProfile />} />
                <Route path="/update_dev_profile" element={<UpdateDevProfile />} />

                <Route path="/company_signin" element={<CompanySignIn />} />
                <Route path="/company_signup" element={<CompanyRegister />} />
                <Route path="/create_company_contracts" element={<CreateCompanyContracts />} />
                <Route path="/company_profile" element={<CompanyProfile />} />
                <Route path="/view_company_profile" element={<UpdateCompanyProfile />} />
            </Routes>
        </BrowserRouter>);
};

const rootElement = document.getElementById("root");
ReactDOM.render(<App />, rootElement);
