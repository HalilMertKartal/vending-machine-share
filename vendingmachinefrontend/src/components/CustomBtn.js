import {React, Component} from "react";
import {Button} from "@mui/material";
class CustonBtn extends Component {
    render() {
        const {
            variant = 'contained',
            className = '',
            backgroundColor = '',
            marginTop = '',
            marginLeft = "",
            padding = '18px 36px',
            fontSize = '',
            onClick,
            children,
        } = this.props;

        const buttonStyle = {
            borderRadius: '15px',
            backgroundColor,
            marginTop,
            marginLeft,
            padding,
            fontSize,
          };

        return (
        <Button variant={variant}
                className={className}
                style={buttonStyle}
                onClick = {onClick}
                children ={children}>
        </Button>
        );
    }
}
export default CustonBtn;