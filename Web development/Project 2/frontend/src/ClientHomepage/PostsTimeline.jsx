import Axios from 'axios';

export default function PostsTimeline(props){

    function handleVideo(post){
        if(post.video.split('/')[1] !== ""){
            return (
                <video width="320" height="240" id="video" controls>
                    <source src={post.video}/>
                </video>
            );
        }
    }

    function handleImage(post){
        if(post.image.split('/')[1] !== ""){
            return (
                <><img alt={post.image} src={post.image} height="200" width="300" /><br /></>
            );
        }
    }

    const DisplayPosts = (props) => {
        const {posts} = props;

        if(posts?.length > 0){
            return (
                posts.map((post, index) => {
                    console.log(post);
                    return(
                        <div className="post" key={post._id}>
                            <h3 className="post_caption">{post.caption}</h3>
                            <div>
                                {handleImage(post)}
                                {handleVideo(post)}
                            </div>
                            <span>Geotag: {post.geolocation[0]}, {post.geolocation[1]}</span><br/>
                            <span>Date posted: {post.timestamp}</span><br/>

                            <button onClick={() => {
                                const confirmation = window?.confirm("Are you sure you want to delete this post?");
                                if(confirmation){
                                    Axios.delete(`/api/posts/${post._id}`).then(res => {
                                        console.log(res.data);
                                        window.location.reload();
                                    });
                                }
                                
                            }}>Delete post</button>
                            <button onClick={() => {
                                let caption = window.prompt("Update your caption:");
                                if(caption !== null){
                                    Axios.put(`/api/posts/${post._id}`, {caption: caption}).then(res => {
                                        console.log(res.data);
                                        window.location.reload();
                                    });
                                }
                                
                            }}>Update caption</button>
                            <span></span>
                        </div>
                    )
                })
            )
        } else {
            return (<h3>No posts</h3>)
        }
    } 

    return (
        <>
            {DisplayPosts(props)}
        </>
    )
    
}