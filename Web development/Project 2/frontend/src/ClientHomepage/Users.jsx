import React, { useEffect, useState } from "react";
import { DataGrid } from '@mui/x-data-grid';
import Box from '@mui/material/Box';
import IconButton from '@mui/material/IconButton';
import TextField from '@mui/material/TextField';
import ClearIcon from '@mui/icons-material/Clear';
import SearchIcon from '@mui/icons-material/Search';
import Button from '@mui/material/Button';

export default function SearchTableButton() {
    const [platform, setPlatform] = useState([]);
    const [searchText, setSearchText] = useState('');
    const [rows, setRows] = useState([]);

    useEffect(() => {
        let ListofUsers = [
          //talk to backend on how to retrieve Users in the database
            { id: 1, name: "User", Friends: "filler"},
            { id: 2, name: "Ethan", Friends: "filler"},
            { id: 4, name: "Sam", Friends: "filler"},
            { id: 5, name: "Keagan", Friends: "filler"},
            { id: 6, name: "William", Friends: "filler"},
            { id: 7, name: "Liam", Friends: "filler"},
            { id: 8, name: "Bob", Friends: "filler"},
            { id: 9, name: "Luke", Friends: "filler"},
            { id: 10, name: "Ross", Friends: "filler"},
        ];

        setPlatform(ListofUsers);
        setRows(ListofUsers);
    }, []);

    const columns = [
        { field: 'name', headerName: 'Name', width: 300 },
        { field: 'friends',
          headerName: 'Friends',
          width: 150,
          editable: true,
          renderCell: (params) => (
              <strong>
                {params.value?.getFullYear() ?? ''}
                <Button
                  variant="contained"
                  color="primary"
                  size="medium"
                  style={{ marginLeft: 16 }}
                >
                  Add friend
                </Button>
              </strong>
        )},
    ];

    function escapeRegExp(value) {
        return value.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, '\\$&');
    }

    const requestSearch = (searchValue) => {
        const Search = new RegExp(escapeRegExp(searchValue), 'i');
        const Results = platform.filter((row) => {
            return Object.keys(row).some((field) => {
                return Search.test(row[field].toString());
            });
        });
        setRows(Results);
    };

    return (
        <div>
            <h1>List of Users</h1>
            <div style={{ height: 400, width: '100%' }}>
                <Box>
                    <TextField
                        variant="standard"
                        value={searchText}
                        onChange={(e) => { setSearchText(e.target.value); requestSearch(e.target.value) }}
                        Searchbarfillertxt="..."
                        InputProps={{
                            startAdornment: <SearchIcon fontSize="small" color="action" />,
                            endAdornment: (
                                <IconButton
                                    title="Clear Search"
                                    aria-label="Clear Search"
                                    size="small"
                                    style={{ visibility: searchText ? 'visible' : 'hidden', borderRadius: "57%", paddingRight: "1px", margin: "0", fontSize: "1.25rem" }}
                                    onClick={(e) => {setSearchText(''); setRows(platform)} }
                                >
                                    <ClearIcon fontSize="small" color="action" />
                                </IconButton>
                            ),
                        }}
                        sx={{
                            width: { xs: 1, sm: 'auto' }, m: (theme) => theme.spacing(1, 0.5, 1.5),
                            '& .MuiSvgIcon-root': {
                                mr: 0.5,
                            },
                            '& .MuiInput-underline:before': {
                                borderBottom: 1,
                                borderColor: 'divider',
                            },
                        }}
                    />
                </Box>
                <DataGrid
                    disableColumnMenu
                    rows={rows}
                    columns={columns}
                    pageSize={10}
                    rowsPerPageOptions={[10]}
                />
            </div>
        </div >
    );
}