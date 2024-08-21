import React, { useEffect, useState } from "react";
import { DataGrid } from '@mui/x-data-grid';
import Box from '@mui/material/Box';
import IconButton from '@mui/material/IconButton';
import TextField from '@mui/material/TextField';
import ClearIcon from '@mui/icons-material/Clear';
import SearchIcon from '@mui/icons-material/Search';

export default function DataGridExample() {
    const [platform, setPlatform] = useState([]);
    const [searchText, setSearchText] = useState('');
    const [rows, setRows] = useState([]);

    useEffect(() => {
        let platformList = [
            { id: 1, name: "Infinitbility", doamin: "infinitbility.com" },
            { id: 2, name: "aGuideHub", doamin: "aguidehub.com" },
            { id: 3, name: "SortoutCode", doamin: "sortoutcode.com" },
        ];
        setPlatform(platformList);
        setRows(platformList);
    }, []);

    const columns = [
        { field: 'name', headerName: 'Name', width: 300 },
        { field: 'doamin', headerName: 'Domain', width: 300 },
    ];

    function escapeRegExp(value) {
        return value.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, '\\$&');
    }

    const requestSearch = (searchValue) => {
        const searchRegex = new RegExp(escapeRegExp(searchValue), 'i');
        const filteredRows = platform.filter((row) => {
            return Object.keys(row).some((field) => {
                return searchRegex.test(row[field].toString());
            });
        });
        setRows(filteredRows);
    };

    return (
        <div>
            <h1>Platforms</h1>
            <div style={{ height: 400, width: '100%' }}>
                <Box>
                    <TextField
                        variant="standard"
                        value={searchText}
                        onChange={(e) => { setSearchText(e.target.value); requestSearch(e.target.value) }}
                        placeholder="Search..."
                        InputProps={{
                            startAdornment: <SearchIcon fontSize="small" color="action" />,
                            endAdornment: (
                                <IconButton
                                    title="Clear"
                                    aria-label="Clear"
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