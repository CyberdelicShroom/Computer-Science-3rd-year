import React from "react";
import ReactDOM from "react-dom";

import {
  BrowserRouter, Route, Routes, useNavigate,
  useLocation, Navigate
} from "react-router-dom";

import ClientSignup from "./auth/ClientSignup";
import ClientSignin from "./auth/ClientSignin";
import ClientHomepage from "./ClientHomepage/ClientHomepage";
import ClientProfile from "./ClientHomepage/ClientProfile";
import UpdateClientProfile from "./ClientHomepage/UpdateClientProfile";
import CreatePost from "./ClientHomepage/CreatePost";
import Groups from "./ClientHomepage/Groups";
import CreateGroup from "./ClientHomepage/CreateGroup";
import "./styling/styles.css";
import Users from "./ClientHomepage/Users";



import "./styling/styles.css";

//npm install @mui/x-data-grid
//npm i @material-ui/core
//npm i @material-ui/icons
//npm install @mui/icons-material

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<ClientSignin />}/>
                <Route path="/client_signup" element={<ClientSignup />} />
                <Route path="/client_signin" element={<ClientSignin />} />
                <Route path="/client_homepage" element={<ClientHomepage />} />
                <Route path="/client_profile" element={<ClientProfile />} /> 
                <Route path="/update_client_profile" element={<UpdateClientProfile />} />
                <Route path="/create_post" element={<CreatePost/>} />
                <Route path="/view_groups" element={<Groups />}/>
                <Route path="/create_group" element={<CreateGroup />}/>
                <Route path="/users" element={<Users />}/>
            </Routes>
        </BrowserRouter>);
};

const rootElement = document.getElementById("root");
ReactDOM.render(<App />, rootElement);
