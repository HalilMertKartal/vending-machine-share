import { Paper, Button, FormControl, InputLabel, Select, MenuItem, 
    Card,TextField, Dialog, DialogActions, DialogContent, DialogContentText, 
    DialogTitle } from "@mui/material";
import Snackbar from '@mui/material/Snackbar';
import * as React from 'react';
import { useState, useEffect, useCallback } from "react";
import axios from 'axios';
import { useNavigate } from "react-router-dom";
import AddCircleIcon from '@mui/icons-material/AddCircle';
import PriceChangeIcon from '@mui/icons-material/PriceChange';
import RestartAltIcon from '@mui/icons-material/RestartAlt';
import AttachMoneyIcon from '@mui/icons-material/AttachMoney';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import "../style/Supplier.css";
import * as Constants from "../util/Constants";
import Alert from "../util/AlertUtil";


const reloadWindow = () => {
    window.location.reload();
}

const Supplier = () => {
    /*
    States
    */
    const [id, setID] = useState('1');
    const [reRenderFlag, setFlag] = useState(true);

    const [stocksToUpdate, setStocksToUpdate] = useState({
        stock: 0,
    });

    const [priceToUpdate, setPriceToUpdate] = useState({
        price: 0,
    });

    const [product, setProduct]=useState({
        id:"1",
        name:"",
        stock:"",
        price:""
    });

    const [isConfirmationOpen, setIsConfirmationOpen] = useState(false);
    const [isSnackbarOpen, setSnackbarOpen] = React.useState(false);
    
    const [snackbarMessage, setSnackbarMessage] = React.useState("");
    // Default: success. If you want to change snackbar to another severity, do it
    const [snackbarSeverity, setSnackbarSeverity] = React.useState("success");

    const navigate = useNavigate();

    /*
    HTTP Request functions
    */

    const loadProduct= useCallback( async ()=>{
        const result = await axios.get(`http://localhost:8080/product/get${id}`);
        setProduct(result.data);
    }, [id]);

    const resetMachine=async ()=>{
        await axios.patch(`http://localhost:8080/vending/resetMachine`);
        setTimeout(() => {
            reloadWindow();
        }, Constants.RELOAD_MS); 
    }

    const collectMoney=async ()=>{
        const result = await axios.patch(`http://localhost:8080/vending/collectMoney`);
        console.log(result.data === "Successfully collected: 0.0");
        if (result.data === "Successfully collected: 0.0"){
            // Alert user, money could not be collected
            handleOpenSnackbar(Constants.MONEY_COLLECTION_ERROR, "error");
        }
        else {
            // Alert user, money is collected
            handleOpenSnackbar(result.data, "success");
        }
    }

    const addStocks=async ()=>{
        if (product.stock >= Constants.MAX_STOCKS_INPUT) {
            // Stocks cannot be added
            handleOpenSnackbar(Constants.STOCKS_ADDED_ERROR, "error");
        }
        else {
            const result = await axios.patch(`http://localhost:8080/product/addStocks${id}`, stocksToUpdate);
            // Stocks added successfully
            handleOpenSnackbar(result.data, "success");
        }
 
    }

    const changePrice=async ()=>{
        const result = await axios.patch(`http://localhost:8080/product/changePrice${id}`, priceToUpdate);
        // Price changed successfully
        handleOpenSnackbar(result.data, "success");
    }

    // If id is changed, this callback will be used.
    useEffect(() => {
        loadProduct(id);
    }, [id, reRenderFlag, loadProduct]);


    /*
    Functions to handle input change
    */

    const handleListSelectionChange = (event) => {
        setID(event.target.value);
    };

    const handleAddStocksInputChange = (event) => {
        const stocksInt = Math.floor(Number(event.target.value))
        event.target.value = stocksInt;
        
        if (stocksInt > Constants.MAX_STOCKS_INPUT) {
            event.target.value = String(stocksInt).slice(0, -1);
        } else if (stocksInt < 1) {
            event.target.value = 0;
        }
        console.log("Stocks: " +event.target.value);
        setStocksToUpdate( () => (
            {
                "stock": Number(event.target.value)
            }
        ) );
    };

    const handleChangePriceInputChange = (event) => {
        console.log(stocksToUpdate);
        const priceInt = Number(event.target.value);
        if (priceInt > Constants.MAX_PRICE_INPUT) {
            event.target.value = Constants.MAX_PRICE_INPUT;
        } else if (priceInt < 0) {
            event.target.value = 0;
        }
        setPriceToUpdate( () => (
            {
                "price": Number(event.target.value)
            }
        ) );
    };

    const handleOpenConfirmation = () => {
        setIsConfirmationOpen(true);
    };

    const handleCloseConfirmation = () => {
        setIsConfirmationOpen(false);
    };

    /*
    Functions to handle clicks
    */

    const handleResetClicked = () => {
        handleConfirmAction();
        resetMachine();
    };

    const handleCollectMoneyClicked = () => {
        collectMoney();
    };

    const handleAddStocksClicked = () => {
        setFlag(!reRenderFlag);
        addStocks();
    };

    const handleChangePriceClicked = () => {
        setFlag(!reRenderFlag);
        changePrice();
        
    };

    const handleGoToMachineClicked = () => {
        navigate("/");
    };

    const handleConfirmAction = () => {
        handleOpenConfirmation();
        // Close the confirmation dialog
        handleCloseConfirmation();
        // Alert the user
        handleOpenSnackbar(Constants.RESET_SUCCESS, "success");
    };

    const handleOpenSnackbar = (message, severity) => {
        setSnackbarMessage(message);
        setSnackbarSeverity(severity);
        setSnackbarOpen(true);
      };
    
    const handleCloseSnackbar = (event, reason) => {
    if (reason === 'clickaway') {
    }
    setSnackbarOpen(false);
    };


    return (
        <div className="main">
            <Paper elevation={10} className="paper">
                <h2>Machine Operations</h2>
                <div className="btn-group">
                    <Button variant="contained" color="error" className="btn"
                    endIcon={<RestartAltIcon />} 
                    onClick={handleOpenConfirmation}>
                        <h3>Reset</h3>
                    </Button>
                    <Button variant="contained" color="success" className="btn"
                    endIcon={<AttachMoneyIcon />} 
                    onClick={handleCollectMoneyClicked}>
                        <h3>Collect Money</h3>
                    </Button>
                    <Button variant="contained" color="primary" className="btn"
                    onClick={handleGoToMachineClicked}
                    endIcon={<ArrowForwardIcon />}>
                    <h4>Go To<br/>Machine</h4>
                </Button>
                </div>
            </Paper>
            <div className="white-space"/>
            <Paper elevation={10} className="paper">
                <h2>Product Operations</h2>
                <FormControl fullWidth>
                    <InputLabel id="product-name">Product Name</InputLabel>
                    <Select
                        labelId="product-name-label"
                        id="product-name-select"
                        value={id}
                        label="Product Name"
                        onChange={handleListSelectionChange}
                    >
                        <MenuItem value={1}>Water</MenuItem>
                        <MenuItem value={2}>Coke</MenuItem>
                        <MenuItem value={3}>Soda</MenuItem>
                    </Select>
                </FormControl>
                <h2>Product Details</h2>

                    <Card variant="outlined" className="card">
                        <h4>Name: {product.name}</h4>
                        <h4>Stock: {product.stock}</h4>
                        <h4>Price: {product.price}</h4>
                    </Card>
                    <div className="input-group">
                        <TextField 
                        className="txtField" type="number" 
                        defaultValue={0} onChange={handleAddStocksInputChange}>
                        </TextField>
                        <Button variant="contained" className="btn"
                        onClick={handleAddStocksClicked}
                        endIcon={<AddCircleIcon/>}
                        size="small">
                            <h3>Add Stocks</h3>
                        </Button>
                    </div>
                    <div className="input-group">
                        <TextField
                        className="txtField" type="number"
                        defaultValue={0} onChange={handleChangePriceInputChange}
                        ></TextField>
                        <Button variant="contained" className="btn"
                        onClick={handleChangePriceClicked}
                        endIcon={<PriceChangeIcon />}
                        size="small">
                            <h4>Change Price</h4>
                        </Button>
                    </div>
            </Paper>
            <Dialog
                open={isConfirmationOpen}
                onClose={handleCloseConfirmation}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
            >
                <DialogTitle id="alert-dialog-title">CONFIRM RESET</DialogTitle>
                <DialogContent>
                    <DialogContentText id="alert-dialog-description">
                        Are you sure you want to <b>RESET</b> the machine completely?
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseConfirmation} color="primary">
                        Cancel
                    </Button>
                    <Button onClick={handleResetClicked} color="primary" autoFocus>
                        Confirm
                    </Button>
                </DialogActions>
            </Dialog>
            <Snackbar open={isSnackbarOpen} autoHideDuration={5000} onClose={handleCloseSnackbar}>
                <Alert onClose={handleCloseSnackbar} severity={snackbarSeverity} sx={{ width: '100%' }}>
                    {snackbarMessage}
                </Alert>
            </Snackbar>
        </div>
    );
  };
  
  export default Supplier;