import React from "react";
const Layout = (props) =>
(
    <div>
        <div style={{ padding: "0.5rem" }}>
            {props.children}
        </div>
    </div>
);
export default Layout;

