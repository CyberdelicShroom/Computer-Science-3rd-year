import React, { useState, useEffect } from "react";
import Navbar from "../components/navbar/Navbar";
import Axios from "axios";
import PostsTimeline from "./PostsTimeline";

const ClientHomepage = () => {
    const [posts, setPosts] = useState();

    useEffect(() => {
        getPosts();
    }, [])

    function getPosts(){
        Axios.get("api/posts")
        .then((res) => {
            if(res){
                let posts = res.data;
                setPosts(posts);
            }

        });
    }

    return (
        <div>
            <Navbar isDev />
            
            <PostsTimeline posts={posts}/>
        </div>
    );
};
export default ClientHomepage;