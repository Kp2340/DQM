// src/App.js
import { BrowserRouter, Routes, Route } from "react-router-dom";
import CompletenessCheck from "./CompletenessCheck";
import DomainCheck from "./DomainCheck";
import FormatCheck from "./FormatCheck";

function App() {

    return (
        <>
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<CompletenessCheck/>} />
                    <Route path="/completeness" element={<CompletenessCheck/>} />
                    <Route path="/format" element={<FormatCheck/>} />
                    <Route path="/domain" element={<DomainCheck/>} />
                </Routes>
            </BrowserRouter>
        </>
    )
}
export default App;