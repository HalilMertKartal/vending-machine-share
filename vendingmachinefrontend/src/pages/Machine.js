import { useState, useEffect, useReducer } from "react";
import axios from 'axios';
import "../style/Machine.css";
import useSound from 'use-sound';
import * as Constants from "../util/Constants";

import machineImg from "../images/vending_machine.jpg";
import colaImg from "../images/cola.jpg";
import waterImg from "../images/water.jpg";
import sodaImg from "../images/soda.jpg";

import putMoneySound from '../sounds/put_money.mp3';
import refundSound from '../sounds/refund.mp3'
import okSound from '../sounds/ok.mp3'
import errSound from '../sounds/error.mp3'

import Snackbar from '@mui/material/Snackbar';
import Alert from "../util/AlertUtil";
import CustomBtn from "../components/CustomBtn";

const Machine = () => {
    /*
    States
    */

    const [temperatureInside, setTemperatureInside] = useState(0);
    const [currentMoney, setCurrentMoney] = useState(0);

    const [snackbarMessage, setSnackbarMessage] = useState("");
    // Default: success. If you want to change snackbar to another severity, do it
    const [snackbarSeverity, setSnackbarSeverity] = useState("success");
    const [isSnackbarOpen, setSnackbarOpen] = useState(false);

    const [selectedProductID, setSelectedProductID] = useState(0);
    const [showPicture, setShowPicture] = useState(false);

    /*
    @PARAMS
    state: basically the object of current values of states
    action: passed as the obj that you give into the dispatch()
    */

    // @RETURNS the new version of state
    const reducer = (state, action) => {
        switch(action.type) {
            case Constants.STATE_NAMES.WATER:
                return {
                    ...state,
                    waterPrice : action.payload
                };

            case Constants.STATE_NAMES.COKE:
                return {
                    ...state,
                    cokePrice : action.payload
                };

            case Constants.STATE_NAMES.SODA: 
                return {
                    ...state,
                    sodaPrice : action.payload
                };

            default: return state;
        };
    };

    const initializeStates = () => {
        return {
            waterPrice: 0,
            cokePrice: 0,
            sodaPrice: 0
        };  
    };

    const [priceState, dispatch] = useReducer(reducer, initializeStates);


    const [playPutMoneySound] = useSound(putMoneySound, { volume: 0.1 });
    const [playRefundSound] = useSound(refundSound, { volume: 0.35 });
    const [playOKSound] = useSound(okSound, { volume: 0.15 });
    const [playErrorSound] = useSound(errSound, { volume: 0.25 });

    // Initialize the temperature as fetched from db just once
    const fetchTemperatureData = async ()=> {
        const response = await axios.get(`http://localhost:8080/vending/getTemperature`);
        setTemperatureInside(parseFloat(response.data).toFixed(1));
    };
    const fetchPrices = async ()=> {
        const responseWaterPrice = (await axios
            .get(`http://localhost:8080/product/get1`)).data.price;
        const responseCokePrice = (await axios
            .get(`http://localhost:8080/product/get2`)).data.price;
        const responseSodaPrice = (await axios
            .get(`http://localhost:8080/product/get3`)).data.price;

        const responses = [
            responseWaterPrice, 
            responseCokePrice,
            responseSodaPrice ];

        const names = [ Constants.STATE_NAMES.WATER,
                        Constants.STATE_NAMES.COKE,
                        Constants.STATE_NAMES.SODA ];

        responses.forEach((item, index) => {
            dispatch(
                {
                    type: names[index],
                    payload: item
                }
            );
        }); 
    };

    const fetchCurrentMoney = async ()=> {
        const response = await axios.patch(`http://localhost:8080/vending/putMoney0`);
        /* Response will include current money. This patch call has no effect.
        This is just a workaround.
        A new controller function is needed to properly fetch the data by using a GET request.
        */
        setCurrentMoney(response.data);
    }

    useEffect(() => {
        fetchTemperatureData();
        fetchPrices();
        fetchCurrentMoney();

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
        setCurrentMoney(response.data);
        playPutMoneySound();
    }

    // Options: 1, 2 or 3. This will be triggered when one of the buttons is pressed
    const requestProduct = async(givenID) => {
        const response = await axios.patch(`http://localhost:8080/vending/requestProduct${givenID}`);
        if (response.data === -1) {
            // If a picture is already shown, keep it
            if (!showPicture) {
                handleShowPicture(false, givenID);
            }

            handleOpenSnackbar(Constants.PRODUCT_RETURN_FAILED_MSG, "error");
        
        }
        else {
            // Handle success
            setSelectedProductID(givenID);
            handleShowPicture(true, givenID);
            handleOpenSnackbar(Constants.PRODUCT_RETURN_SUCCESS + response.data, "success");
            playOKSound();
            setCurrentMoney(0);
        }
    }

    // Called when take refund button is clicked
    const takeRefund = async() => {
        const response = await axios.patch(`http://localhost:8080/vending/takeRefund`);
        console.log(response.data); // Refund amount is the response
        if (response.data === 0) {
            // Fail
            handleOpenSnackbar(Constants.REFUND_FAILED, "error");
        }
        else {
            // Success
            handleOpenSnackbar(Constants.REFUND_SUCCEED + response.data, "success");
            playRefundSound();
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
        requestProduct(id);
    }

    const handleRefundBtnClicked = () => {
        takeRefund();
        setCurrentMoney(0);
    }


    const handleOpenSnackbar = (message, type) => {
        setSnackbarMessage(message);
        setSnackbarSeverity(type);
        setSnackbarOpen(true);
        if (type === "error") {
            playErrorSound();
        }
      };
    
    const handleCloseSnackbar = (event, reason) => {
    if (reason === 'clickaway') {
    }
    setSnackbarOpen(false);
    };

    const imgs = [waterImg, colaImg, sodaImg];

    const handleShowPicture =(bool, givenID) => {
;
        if (bool && givenID !== 0) {
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
                        alt={`${index}`}
                        className="grid-image"
                    />
                </div>
            ))}
            </div>
            <div className="panel">
                
                <div className="money-btn-group">
                <text><b>Put money below</b></text>
                    <CustomBtn 
                        className="money-btn"
                        backgroundColor={moneyBtnColor}
                        marginTop="10px"
                        fontSize= "35px"
                        onClick={() => handleMoneyBtnClicked(1)}>
                        1
                    </CustomBtn>
                    
                    <CustomBtn 
                        className="money-btn"
                        backgroundColor={moneyBtnColor}
                        marginTop="10px"
                        marginLeft="10px"
                        fontSize= "35px"
                        onClick={() => handleMoneyBtnClicked(5)}>
                        5
                    </CustomBtn>
                    
                    <CustomBtn 
                        className="money-btn"
                        backgroundColor={moneyBtnColor}
                        marginTop="10px"
                        fontSize= "35px"
                        onClick={() => handleMoneyBtnClicked(10)}>
                        10
                    </CustomBtn>

                    <CustomBtn 
                        className="money-btn"
                        backgroundColor={moneyBtnColor}
                        marginTop="10px"
                        marginLeft="10px"
                        fontSize= "35px"
                        onClick={() => handleMoneyBtnClicked(20)}>
                        20
                    </CustomBtn>
                </div>

                <div className="show-current-money">
                <text>{currentMoney}$</text>
                </div>
                
                <div className="product-button-group">
                    <text><b>Select a product below</b></text>
                    <CustomBtn 
                        className="product-btn"
                        backgroundColor="#21b6ae"
                        marginTop="10px"
                        fontSize= "30px"
                        onClick={() => handleProductBtnClicked(1)}>
                        Water({priceState.waterPrice}$)
                    </CustomBtn>
                    
                    <CustomBtn 
                        className="product-btn"
                        backgroundColor="#C70525"
                        marginTop="10px"
                        fontSize= "30px"
                        onClick={() => handleProductBtnClicked(2)}>
                        Coke({priceState.cokePrice}$)
                    </CustomBtn>

                    <CustomBtn 
                        className="product-btn"
                        backgroundColor="green"
                        marginTop="10px"
                        fontSize= "30px"
                        onClick={() => handleProductBtnClicked(3)}>
                        Soda({priceState.sodaPrice}$)
                    </CustomBtn>

                </div>

                <div className="refund-group">
                    <text>
                        <b>Take Refund</b>
                    </text>

                    <CustomBtn 
                        className="refund-btn"
                        backgroundColor="black"
                        marginTop="10px"
                        marginLeft= "23px"
                        fontSize= "25px"
                        onClick={handleRefundBtnClicked}>

                        REFUND
                    </CustomBtn>

                </div>
            </div>
            <div className="return-area"
            onClick={() => setShowPicture(false)}>
                <img className="return-img"
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