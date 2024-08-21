import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';

import CardContent from '@material-ui/core/CardContent';
import CardMedia from '@material-ui/core/CardMedia';
import Typography from '@material-ui/core/Typography';
import { Collapse } from '@material-ui/core';
import {useNavigate} from "react-router";

const useStyles = makeStyles({
  root: {
    maxWidth: 645,
    background: 'rgba(0,0,0,0.5)',
    margin: '20px',
  },
  media: {
    height: 440,
  },
  title: {
    fontFamily: 'Nunito',
    fontWeight: 'bold',
    fontSize: '2rem',
    color: '#05595B',
  },
  desc: {
    fontFamily: 'Nunito',
    fontSize: '1.1rem',
    color: '#ddd',
  },

});

export default function ImageCard({ place, checked,num }) {
  const classes = useStyles();

  let navigate = useNavigate();
  const routeChange = () =>{
    let path = "";
    if(num === 0){
       path = "/client_signin";
    }
    else if(num == 1){
       path = "/company_signin";
    }else{
      path = "/";
    }
    navigate(path);
  }



  return (
    <Collapse  className='cursor-pointer'  in={checked} {...(checked ? { timeout: 1000 } : {})}>
      <Card className={classes.root}>
        <CardMedia
            onClick={routeChange}
          className={classes.media}
          image={place.imageUrl}
          title="Click on the image to register/login as a Developer or Company"
        />
        <CardContent>
          <Typography
            gutterBottom
            variant="h5"
            component="h1"
            className={classes.title}
          >
            {place.title}
          </Typography>
          <Typography
            variant="body2"
            color="textSecondary"
            component="p"
            className={classes.desc}
          >
            {place.description}
          </Typography>
        </CardContent>
      </Card>
    </Collapse>
  );
}
