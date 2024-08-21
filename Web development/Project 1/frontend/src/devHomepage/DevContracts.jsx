import MaterialTable from "material-table";
import tableIcons from "../styling/styles.css";
import * as React from 'react';
import Checkbox from '@mui/material/Checkbox';
import {TableContainer,Table,TableHead,TableRow,TableCell,TableBody,Container} from "@mui/material";
import { useState} from "react";

function DevContrancts() {

const data = [
  // retrieve company and contract data from backend

];
let ArrayPop = [
  {CompNames : "WIlliam", ContractVal: "10,000", PrefLang:"Java", JobLoct: "Remote", DatePost: "10/03/99", AccDeny: "Accepted" },
  {CompNames : "Facebutt", ContractVal: "idiot", PrefLang:"C++", JobLoct: "In office", DatePost: "Today", AccDeny: "Accepted"},
  {CompNames : "Bob", ContractVal: "the builder", PrefLang:"Python", JobLoct: "Remote", DatePost: "Today", AccDeny: "Accepted"}
];
  return (
    <Container className = "DevContracts">
        <Table>
         <TableHead
          >
            Current Develepor Contracts
            <TableRow>
              <TableCell>Contract</TableCell> 
              <TableCell>Preferred Languages</TableCell>  
              <TableCell>Contract Value</TableCell>  
              <TableCell>Job Location</TableCell>
              <TableCell>Date Posted</TableCell>
              <TableCell>Accepeted or Denied</TableCell>          
                </TableRow>
                   </TableHead>
                    <TableBody>
                    {ArrayPop.map((field, key) => {
                         return (
                       <TableRow key={key}>
                      <TableCell>{field.CompNames}</TableCell>
                      <TableCell>{field.PrefLang}</TableCell>
                   <TableCell>{field.ContractVal}</TableCell>
                   <TableCell>{field.JobLoct}</TableCell>
                   <TableCell>{field.DatePost}</TableCell>
                   <TableCell>{field.AccDeny}</TableCell>
                 </TableRow>
                )
               })}
          </TableBody>
        </Table>
    </Container>
  );
};

export default DevContrancts;