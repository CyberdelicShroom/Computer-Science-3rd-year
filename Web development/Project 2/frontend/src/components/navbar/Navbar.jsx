import React from "react";
import { Link } from "react-router-dom";

const Navbar = (props) => {
    return (
        <div className="flex gap-4 p-3 mb-3 bg-green-800  ">
            {
                <>
                    <Link to="/client_homepage" className="text-2xl">Feed</Link>
                    <Link to="/client_profile" className="text-2xl">My Profile</Link>
                    <Link to="/view_groups" className="text-2xl">Groups</Link>
                    <Link to="/create_post" className="text-2xl">Post</Link>
                    <Link to="/users" className="text-2xl">Users</Link>
                </>
            }

        </div>
    );
};
export default Navbar;