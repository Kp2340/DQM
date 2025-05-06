 import React, {useState} from "react";
import axios from "axios";

function FormatCheck() {
    const[selectedFormat, setSelectedFormat] = useState("");
    const[columns, setColumns] = useState([]);
    const[selectedColumns, setSelectedColumns] = useState([]);
    const[errorRate, setErrorRate] = useState(0);
    const[fileFormat, setFileFormat] = useState([]);
    const [logs, setLogs] = useState([]);

    const handleFileUpload = async (event) => {
        const uploadedFile = event.target.files[0];
        const formData = new FormData();
        formData.append('file', uploadedFile);
        try {
            const response = await axios.post('http://dqm-iw1g.onrender.com:3001/upload/f', formData);
            setColumns(response.data);
            setSelectedColumns([]);
        } catch (error) {
            console.error('Error uploading file:', error);
        }
    }
    const handleColumnSelect = (column) => {
        setSelectedColumns((prev) =>
            prev.includes(column) ? prev.filter((c) => c !== column) : [...prev, column]
        )
    }
    const handleCalculate = async () => {
        const formData = new FormData();
        formData.append('columns', JSON.stringify(selectedColumns));
        try {
            const response = await axios.post(`http://dqm-iw1g.onrender.com:3001/calculate/${selectedFormat}`, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });
            const data = response.data;
            console.log(data)
            if(selectedFormat==="file"){ setFileFormat(data);}
            else setErrorRate(data.errorRate || 0.0);
        } catch (error) {
            console.error('Error calculating error rates:', error);
        }
    };
    const handleSave = async () => {
        try {
            await axios.post(`http://dqm-iw1g.onrender.com:3001/save/${selectedFormat}`);
            axios.get(`http://dqm-iw1g.onrender.com:3001/all/${selectedFormat}`).then(response => {
                if (response && response.data) {
                    setLogs(response.data);
                }
            });
        } catch (error) {
            console.log('Error saving rates:', error);
        }
    };

    const handleDelete = async (id) => {
        try {
            console.log(id);
            await axios.delete(`http://dqm-iw1g.onrender.com:3001/delete/f/${id}`);
            // Refresh the logs after deletion
            axios.get(`http://dqm-iw1g.onrender.com:3001/all/${selectedFormat}`).then(response => {
                if (response && response.data) {
                    setLogs(response.data);
                }
            });
        } catch (error) {
            console.error('Error deleting log entry:', error);
        }
    };
    const handleDeleteAll = async (str) => {
        try {
            await axios.delete(`http://dqm-iw1g.onrender.com:3001/delete/f/all/${str}`);
            axios.get(`http://dqm-iw1g.onrender.com:3001/all/${selectedFormat}`).then(response => {
                if (response && response.data) {
                    setLogs(response.data);
                }
            });
        } catch (error) {
            console.error('Error deleting all log entries:', error);
        }
    };

    return(
        <div style={{fontFamily: "'Roboto', sans-serif", backgroundColor: '#f5f5f5', minHeight: '100vh', padding: '20px', boxSizing: 'border-box',}}>
        <header style={{backgroundColor: '#4B2E83', padding: '15px 20px', color: 'white', display: 'flex', alignItems: 'center', justifyContent: 'space-between', boxShadow: '0 2px 5px rgba(0, 0, 0, 0.1)', borderRadius: '5px', marginBottom: '20px',}}>
            <h1 style={{flexGrow: 1, textAlign: 'center', margin: 0, marginLeft: '250px', fontSize: '24px', fontWeight: '500', color: 'white'}}>
                Format Check
            </h1>
            <a href={'http://dqm-iw1g.onrender.com:3000/domain'} style={{marginRight: '25px', color: "white", textDecoration: "none"}}>Domain Check</a>
            <a href={'http://dqm-iw1g.onrender.com:3000/completeness'} style={{color: "white", textDecoration: "none"}}>Completeness Check</a>
        </header>
            <div style={{ textAlign: "center", marginTop: "20px" }}>
                <select
                    value={selectedFormat}
                    onChange={(e) => {
                        setColumns([]);
                        setSelectedColumns([]);
                        setFileFormat([]);
                        setErrorRate(0);
                        setSelectedFormat(e.target.value);
                        axios.get(`http://dqm-iw1g.onrender.com:3001/all/${e.target.value}`)
                            .then(response => {
                                if (response && response.data) {
                                    setLogs(response.data);
                                }
                            })
                            .catch(error => {
                                console.error('Error fetching logs:', error);
                            });
                    }}
                    style={{padding: "10px", backgroundColor: "#f7f7f7", color: "#333", border: "1px solid #ccc", borderRadius: "5px", cursor: "pointer", fontSize: "16px", outline: "none",}}
                >
                    {/* correct here for the zone and the code option for the railway zone and railway code */}
                    <option value="f/a">Select the option</option>
                    <option value="file">File Format</option>
                    <option value="date">Date Format</option>
                    <option value="stationcode">Station Code Format</option>
                    <option value="latlong">Lat Long Format</option>
                    <option value="railwaycode">Railway Code Format</option>
                    <option value="pincode">Pincode Format</option>
                    <option value="state">State Format</option>
                    <option value="district">District Format</option>
                    <option value="unionterritories">Territories Format</option>
                    <option value="phonenum">Phone Number Format</option>
                </select>
                {selectedFormat!== "" &&
                    <div style={{backgroundColor: '#e6f0fa', padding: '20px', borderRadius: '8px', boxShadow: '0 2px 10px rgba(0, 0, 0, 0.05)', marginBottom: '20px', textAlign: 'center',}}>
                        <h2 style={{fontSize: '20px', fontWeight: '500', color: '#333', marginBottom: '15px',}}>
                            Upload Excel File
                        </h2>
                        <input type="file" onChange={handleFileUpload} accept=".xlsx, .xls" style={{cursor: 'pointer', padding: '10px', border: '1px solid #ddd', borderRadius: '5px', fontSize: '16px', width: '300px', display: 'block', margin: '0 auto', color: '#555', backgroundColor: '#fafafa',}}/>
                    </div>
                }
                {columns.length>0 && <div style={{backgroundColor: '#e8f5e9', padding: '20px',borderRadius: '8px',boxShadow: '0 2px 10px rgba(0, 0, 0, 0.05)',marginBottom: '20px',}}>
                    <h3 style={{fontSize: '18px', fontWeight: '500', color: '#333', textAlign: 'center', marginBottom: '15px',}}>
                        Select Columns
                    </h3>
                    <table style={{width: '100%', maxWidth: '600px', margin: '0 auto', borderCollapse: 'collapse', backgroundColor: '#fff',}}>
                        <thead>
                        <tr>
                            <th style={{border: '1px solid #ddd', padding: '12px', textAlign: 'center', backgroundColor: '#f0f0f0', fontWeight: '500', color: '#333',}}>
                                Select
                            </th>
                            <th style={{border: '1px solid #ddd', padding: '12px', backgroundColor: '#f0f0f0', fontWeight: '500', color: '#333',}}>
                                Column Name
                            </th>
                            {selectedFormat==="file" && <th style={{border: '1px solid #ddd', padding: '12px', textAlign: 'center', backgroundColor: '#f0f0f0', fontWeight: '500', color: '#333',}}>
                                Primary Key
                            </th>}
                        </tr>
                        </thead>
                        <tbody>
                        {columns.map((col, index) => (
                            <tr key={index} style={{
                                transition: 'background-color 0.2s',
                            }}
                                onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#f9f9f9'}
                                onMouseLeave={(e) => e.currentTarget.style.backgroundColor = 'transparent'}>
                                <td style={{border: '1px solid #ddd', padding: '12px', textAlign: 'center',}}>
                                    <input type="checkbox" defaultChecked={false} value={col} onChange={(e) => handleColumnSelect(e.target.value)} style={{cursor: 'pointer', width: '18px', height: '18px', accentColor: '#4B2E83', borderColor: '#4B2E83',}}/>
                                </td>
                                <td style={{border: '1px solid #ddd', padding: '12px', color: '#555',}}>
                                    {col}
                                </td>
                                {selectedFormat==="file" && <td style={{border: '1px solid #ddd', padding: '12px', color: '#555',}}>
                                    {selectedColumns.includes(col) && (fileFormat[col] ? 'Yes' : 'No')}
                                </td>}
                            </tr>
                        ))}
                        </tbody>
                    </table>

                    {columns.length > 0 && (
                        <button
                            onClick={handleCalculate}
                            style={{display: 'block', margin: '20px auto', padding: '10px 20px', backgroundColor: '#4B2E83', color: 'white', border: 'none', borderRadius: '5px', fontSize: '16px', cursor: 'pointer', transition: 'background-color 0.3s',}}
                            onMouseEnter={(e) => e.target.style.backgroundColor = '#5C3A9E'}
                            onMouseLeave={(e) => e.target.style.backgroundColor = '#4B2E83'}
                        >
                            Check {selectedFormat} Format Consistency
                        </button>
                    )}
                    {(errorRate!==0.0 || fileFormat!==[])&&(
                        <div style={{backgroundColor: '#fff3e0', padding: '20px',borderRadius: '8px',boxShadow: '0 2px 10px rgba(0, 0, 0, 0.05)',marginBottom: '20px',display: 'flex',justifyContent: 'center',alignItems: 'center',gap: '30px',}}>
                            <h3 style={{fontSize: '24px',fontWeight: '500',color: '#d9534f',margin: 0,}}>
                                Error Rate: {errorRate}
                            </h3>
                            <button
                                onClick={handleSave}
                                style={{padding: '10px 20px', backgroundColor: '#28a745', color: 'white', border: 'none', borderRadius: '5px', fontSize: '16px', cursor: 'pointer', transition: 'background-color 0.3s',}}
                                onMouseEnter={(e) => e.target.style.backgroundColor = '#218838'}
                                onMouseLeave={(e) => e.target.style.backgroundColor = '#28a745'}
                            >
                                Save
                            </button>
                        </div>
                    )}
                </div>}

                <div style={{backgroundColor: '#fce4ec',padding: '20px',borderRadius: '8px',boxShadow: '0 2px 10px rgba(0, 0, 0, 0.05)',}}>
                    <table style={{width: '100%', borderCollapse: 'collapse', backgroundColor: '#fff',}}>
                        <thead>
                        <tr>
                            {/*<th style={{border: '1px solit #ddd', padding: '12px', backgroundColor: '#f0f0f0', fontWeight: '500', color: '#333'}}>
                                Id
                            </th>*/}
                            <th style={{border: '1px solid #ddd', padding: '12px', backgroundColor: '#f0f0f0', fontWeight: '500', color: '#333'}}>
                                Filename
                            </th>
                            <th style={{border: '1px solid #ddd', padding: '12px', backgroundColor: '#f0f0f0', fontWeight: '500', color: '#333'}}>
                                Attributes
                            </th>
                            {selectedFormat!=="file" && <th style={{border: '1px solid #ddd', padding: '12px', backgroundColor: '#f0f0f0', fontWeight: '500', color: '#333'}}>
                                Error Rate (%)
                            </th>}
                            {selectedFormat==="file" && <th style={{border: '1px solid #ddd', padding: '12px', backgroundColor: '#f0f0f0', fontWeight: '500', color: '#333'}}>
                                Primary Key
                            </th>}
                            <th style={{border: '1px solid #ddd', padding: '12px', backgroundColor: '#f0f0f0', fontWeight: '500', color: '#333'}}>
                                Action
                            </th>
                        </tr>
                        </thead>
                        <tbody>
                        {logs.length > 0 ? (
                            logs.map(log => (
                                <tr key={log.id} style={{transition: 'background-color 0.2s',}}
                                    onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#f9f9f9'}
                                    onMouseLeave={(e) => e.currentTarget.style.backgroundColor = 'transparent'}>
                                    {/*<td style={{border: '1px solid #ddd', padding: '12px', color: '#555'}}>
                                        {log.id || 'N/A'}
                                    </td>*/}
                                    <td style={{border: '1px solid #ddd', padding: '12px', color: '#555'}}>
                                        {log.fileName || 'N/A'}
                                    </td>
                                    <td style={{border: '1px solid #ddd', padding: '12px', color: '#555'}}>
                                        {log.attributes || 'N/A'}
                                    </td>
                                    {selectedFormat!=="file" && <td style={{border: '1px solid #ddd', padding: '12px', color: '#5cb85c'}}>
                                        {log.errorRate || 'N/A'}
                                    </td>}
                                    {selectedFormat==="file" && <td style={{border: '1px solid #ddd', padding: '12px', color: '#5cb85c'}}>
                                        {log.data || ''}
                                    </td>}
                                    <td style={{border: '1px solid #ddd', padding: '12px', textAlign: 'center'}}>
                                        <button
                                            onClick={() => handleDelete(log.id)}
                                            style={{padding: '8px 12px', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: '5px', fontSize: '14px', cursor: 'pointer', transition: 'background-color 0.3s'}}
                                            onMouseEnter={(e) => e.target.style.backgroundColor = '#c82333'}
                                            onMouseLeave={(e) => e.target.style.backgroundColor = '#dc3545'}
                                        >
                                            Delete
                                        </button>
                                    </td>
                                </tr>
                            ))) : (
                            <tr>
                                <td colSpan="5" style={{padding: '20px', textAlign: 'center', color: '#777', fontStyle: 'italic'}}>
                                    No issues found.
                                </td>
                            </tr>
                        )}
                        </tbody>
                    </table>
                    {/* Add Delete All Button */}
                    <div style={{ textAlign: 'right', marginTop: '15px' }}>
                        <button
                            onClick={() => handleDeleteAll(selectedFormat)}
                            style={{padding: '8px 15px', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: '5px', fontSize: '14px', cursor: 'pointer', transition: 'background-color 0.3s'}}
                            onMouseEnter={(e) => e.target.style.backgroundColor = '#c82333'}
                            onMouseLeave={(e) => e.target.style.backgroundColor = '#dc3545'}
                        >
                            Delete All
                        </button>
                    </div>
                </div>
                <div className="alert alert-primary" style={{ margin: "20px 100px" }}>
                    <b>Definition: </b> Format Consistency ensures that data values adhere
                    to the specified format, structure and encoding rules
                    <br />
                </div>
            </div>
    </div>
    )
}
export default FormatCheck;
