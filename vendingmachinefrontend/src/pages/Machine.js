import { useState, useEffect, forwardRef } from "react";
import axios from 'axios';
import "../style/Machine.css";
import * as Constants from "../util/Constants";
import machineImg from "../images/vending_machine.jpg";
import colaImg from "../images/cola.jpg";
import waterImg from "../images/water.jpg";
import sodaImg from "../images/soda.jpg";
import { Button } from "@mui/material";
import Snackbar from '@mui/material/Snackbar';
import Alert from "../util/AlertUtil";

const Machine = () => {
    /*
    States
    */

    const [temperatureInside, setTemperatureInside] = useState(0);

    const [snackbarMessage, setSnackbarMessage] = useState("");
    // Default: success. If you want to change snackbar to another severity, do it
    const [snackbarSeverity, setSnackbarSeverity] = useState("success");
    const [isSnackbarOpen, setSnackbarOpen] = useState(false);

    const [selectedProductID, setSelectedProductID] = useState(0);

    const [showPicture, setShowPicture] = useState(false);

    // Initialize the temperature as fetched from db just once
    const fetchTemperatureData = async ()=> {
        const response = await axios.get(`http://localhost:8080/vending/getTemperature`);
        setTemperatureInside(parseFloat(response.data).toFixed(1));
    }

    useEffect(() => {
        fetchTemperatureData();
    }, []);

    useEffect(() => {
        // Calls dummyCooling every second
        dummyCooling();
        const intervalId = setInterval(dummyCooling, 1000);
        return () => {
            clearInterval(intervalId);
        };
    }, []);

    /*
    HTTP Request functions
    */

    // Called automatically
    const dummyCooling= async () => {
        const afterCooling = await axios.patch(`http://localhost:8080/vending/cooling`);
        setTemperatureInside(parseFloat(afterCooling.data).toFixed(1));

    }

    // Called according to user actions
    

    // Options, 1, 5, 10, 20. This will be triggered when one of the buttons is pressed
    const putMoney = async(givenMoney) => {
        const response = await axios.patch(`http://localhost:8080/vending/putMoney${givenMoney}`);
        handleOpenSnackbar(Constants.PUT_MONEY_MSG + givenMoney + ". Total credit is: "+response.data, "success");
    }

    // Options: 1, 2 or 3. This will be triggered when one of the buttons is pressed
    const requestProduct = async(givenID) => {
        const response = await axios.patch(`http://localhost:8080/vending/requestProduct${givenID}`);
        if (response.data === -1) {
            handleOpenSnackbar(Constants.PRODUCT_RETURN_FAILED_MSG, "error");
            handleShowPicture(false);
        }
        else {
            // Handle success
            handleShowPicture(true);
            handleOpenSnackbar(Constants.PRODUCT_RETURN_SUCCESS + response.data, "success");
            
        }
    }

    // Called when take refund button is clicked
    const takeRefund = async() => {
        const response = await axios.patch(`http://localhost:8080/vending/takeRefund`);
        console.log(response.data); // Refund amount is the response
        if (response.data === 0) {
            handleOpenSnackbar(Constants.REFUND_FAILED, "error");
        }
        else {
            handleOpenSnackbar(Constants.REFUND_SUCCEED + response.data, "success");
        }
    }


    /*
    Functions to handle clicks
    */

    // Change state variables
    const handleMoneyBtnClicked = (amount) => {
        putMoney(amount);
    }

    const handleProductBtnClicked = (id) => {
        setSelectedProductID(id);
        requestProduct(id);
    }

    const handleRefundBtnClicked = () => {
        takeRefund();
    }


    const handleOpenSnackbar = (message, type) => {
        setSnackbarMessage(message);
        setSnackbarSeverity(type);
        setSnackbarOpen(true);

      };
    
    const handleCloseSnackbar = (event, reason) => {
    if (reason === 'clickaway') {
    }
    setSnackbarOpen(false);
    };

    const imgs = [waterImg, colaImg, sodaImg];

    const handleShowPicture =(bool) => {
        if (bool && selectedProductID !== 0) {
            setShowPicture(true);
        } else {
            setShowPicture(false);
        }
    }

    const getImageUrlForIndex = (index) => {
        if (index < 6) {
            return waterImg;
        } else if (index < 12) {
            return colaImg;
        } else {
            return sodaImg;
        }
    }


    const moneyBtnColor = "darkgreen";
    return (
        <div className="container">
            <img
                src={machineImg}
                alt="Machine"
                className="machine-image"
            />
            <div className="show-temperature">
                <text>{temperatureInside}⁰C</text>
            </div>
            <div className="grid-container">
            {Array.from({ length: 6 * 3 }).map((_, index) => (
                <div key={index} className="grid-item">
                    <img
                        src={`${getImageUrlForIndex(index)}`}
                        alt={`Image ${index}`}
                        className="grid-image"
                    />
                </div>
            ))}
            </div>
            <div className="panel">
                
                <div className="money-btn-group">
                <text><b>Put money below</b></text>
                    <Button variant="contained" className="money-btn"
                    style={{
                    borderRadius: 15,
                    backgroundColor: moneyBtnColor,
                    marginTop: "10px",
                    padding: "18px 36px",
                    fontSize: "35px"
                        }}
                        onClick={() => handleMoneyBtnClicked(1)}>
                        1
                    </Button>
                    <Button variant="contained" className="money-btn"
                    style={{
                        borderRadius: 15,
                        backgroundColor: moneyBtnColor,
                        padding: "18px 36px",
                        marginTop: "10px",
                        marginLeft:"10px",
                        fontSize: "35px"
                            }}
                            onClick={() => handleMoneyBtnClicked(5)}>
                        5
                    </Button>
                    <Button variant="contained" className="money-btn"
                    style={{
                        borderRadius: 15,
                        backgroundColor: moneyBtnColor,
                        padding: "18px 36px",
                        marginTop: "15px",
                        fontSize: "35px"
                            }}
                            onClick={() => handleMoneyBtnClicked(10)}>
                        10
                    </Button>
                    <Button variant="contained" className="money-btn"
                    style={{
                        borderRadius: 15,
                        backgroundColor: moneyBtnColor,
                        padding: "18px 36px",
                        marginLeft:"10px",
                        marginTop: "15px",
                        fontSize: "35px"
                            }}
                            onClick={() => handleMoneyBtnClicked(20)}>
                        20
                    </Button>
                </div>
                
                <div className="product-button-group">
                    <text><b>Select a product below</b></text>
                    <Button variant="contained" className="product-btn"
                    style={{
                    borderRadius: 15,
                    backgroundColor: "#21b6ae",
                    marginTop: "10px",
                    padding: "18px 36px",
                    fontSize: "30px"
                        }}
                        onClick={() => handleProductBtnClicked(1)}>
                        Water
                    </Button>
                    <Button variant="contained" className="product-btn"
                    style={{
                        borderRadius: 15,
                        backgroundColor: "#C70525",
                        padding: "18px 36px",
                        marginTop: "10px",
                        fontSize: "30px"
                            }}
                            onClick={() => handleProductBtnClicked(2)}>
                        Coke
                    </Button>
                    <Button variant="contained" className="product-btn"
                    style={{
                        borderRadius: 15,
                        backgroundColor: "green",
                        padding: "18px 36px",
                        marginTop: "10px",
                        fontSize: "30px"
                            }}
                            onClick={() => handleProductBtnClicked(3)}>
                        Soda
                    </Button>
                </div>

                <div className="refund-group">
                    <text>
                        <b>Take Refund</b>
                    </text>
                    <Button variant="contained" className="refund-btn"
                    style={{
                        borderRadius: 15,
                        backgroundColor: "black",
                        padding: "18px 36px",
                        marginTop: "10px",
                        marginLeft: "23px",
                        fontSize: "25px"
                            }}
                            onClick={handleRefundBtnClicked}>
                        REFUND
                    </Button>
                </div>
            </div>
            <div className="return-area">
                <img className="product-img"
                    hidden={!showPicture}
                    src={imgs[selectedProductID-1]}
                    alt="Machine"
                    
                />                        
            </div>
            <Snackbar open={isSnackbarOpen} autoHideDuration={5000} onClose={handleCloseSnackbar}>
                <Alert onClose={handleCloseSnackbar} severity={snackbarSeverity} sx={{ width: '100%' }}>
                    {snackbarMessage}
                </Alert>
            </Snackbar>
        </div>
    );
  };
  
  export default Machine;