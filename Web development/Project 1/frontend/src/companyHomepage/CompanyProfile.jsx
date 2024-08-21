import { useNavigate } from "react-router-dom";
import { useState } from "react"

const CompanyProfile = () => {

    const navigate = useNavigate();
    const [companyname, setCompanyName] = useState();
    const [type, setCompanyType] = useState();
    const [email, setEmail] = useState();
    const [imgSrc, setImgSrc] = useState();
    
    async function deleteCompanyProfile() {
        const options = {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            },
        };
        const response = await fetch("/deleteProfile", options);
        const data = await response.text();
        alert(data + "'s profile has been deleted.");
        navigate("/company_signin");
    }

    async function logout() {
        const options = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            },
        };
        const response = await fetch("/logout", options);
        const data = await response.json();
        if(data.logout_status){
            navigate("/company_signin");
            console.log(data);
        } else {
            console.error(data);
        }
    }

    async function getMoneySpent() {
        const options = {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            },
        };
    }

    async function getCompanyName() {
        const options = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            },
        };
        const response = await fetch("/getCompanyInfo", options);
        const data = await response.json();
        setCompanyName(data.name);
    }

    async function getCompanyType() {
        const options = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            },
        };
        const response = await fetch("/getCompanyInfo", options);
        const data = await response.json();
        if(data.get_company_info){
            setCompanyType(data.type);
        } else {
            console.error(data)
            alert(data.details);
            navigate("/company_signin");
        }
    }

    async function getEmail() {
        const options = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            },
        };
        const response = await fetch("/getCompanyInfo", options);
        const data = await response.json();
        setEmail(data.email);
    }

    async function getLogo() {
        const options = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            },
        };
        const response = await fetch("/displayLogo", options);
        const data = await response.text();
        setImgSrc(data);
    }

    getCompanyName();
    getCompanyType();
    getEmail();
    getLogo();

    return (
        <div>
            <img id="logo" src={imgSrc} height="200" width="300"></img>
            <label>Your Company:</label>
            <p>{companyname}</p>
            <label>Email:</label>
            <p>{email}</p>
            <label>General Industry:</label>
            <p>{type}</p>
            <label>Money spent on contracts:</label>
            <p id="money">N/A</p>
            <button onClick={() => {
                const confirmation = window?.confirm("Are you sure you want to delete your profile?");
                if (confirmation) {
                    deleteCompanyProfile();
                }
            }}>Delete Profile</button>

            <button onClick={() => { navigate("/view_company_profile"); }}>Edit Profile</button>
            <button onClick={() => { logout(); }}>Log out</button>
        </div>
    );
};
export default CompanyProfile;
