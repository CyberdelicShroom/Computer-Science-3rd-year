import React from "react";
import { Link } from "react-router-dom";

const Navbar = (props) => {
    return (
        <div className="flex gap-4 p-3 mb-3 bg-green-800  ">
            {props.isDev ?
                <>
                <Link to="/dev_contracts" className="text-2xl">Contracts</Link> 
                <Link to="/dev_profile" className="text-2xl">Profile</Link></>
                :
                <> 
                <Link to="/company_contracts" className="text-2xl">Contracts</Link>
                <Link to="/company_profile" className="text-2xl">Profile</Link>
                </>
            }

        </div>
    );
};
export default Navbar;