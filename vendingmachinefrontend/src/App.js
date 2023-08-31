import Supplier from "./pages/Supplier";
import Machine from "./pages/Machine";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import './App.css';
import { useEffect } from "react";
import axios from 'axios';

function App() {

  return (
    <BrowserRouter>
      <Routes>
        <Route index element={<Machine />} />
        <Route path="supplier" element={<Supplier />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
