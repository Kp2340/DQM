import React, {useEffect, useState} from "react";
import axios from "axios";

function DomainCheck() {
    const [columns, setColumns] = useState([]);
    const [type, setType] = useState([]);
    const [min, setMin] = useState([]);
    const [max, setMax] = useState([]);
    const [newMin, setNewMin] = useState([]);
    const [newMax, setNewMax] = useState([]);
    const [total, setTotal] = useState([]);
    const [count, setCount] = useState([]);
    const [list, setList] = useState([]);
    // const [exists, setExists] = useState([]);
    // const [selectedList, setSelectedList] = useState([]);
    const [values, setValues] = useState([]);
    const [inconsistency, setInconsistency] = useState([]);
    const [domains, setDomains] = useState([]);
    const [average, setAverage] = useState(0);
    let URL=


    useEffect(() => {
        axios.get('http://dqm-iw1g.onrender.com/all/d')
            .then(response => {
                if (response && response.data) {
                    setDomains(response.data);
                }
            })
            .catch(error => {
                console.error('Error fetching logs:', error);
            });
    }, []);

    const handleFileUpload = async (event) => {
        const uploadedFile = event.target.files[0];
        const formData = new FormData();
        formData.append('file', uploadedFile);

        try {
            const response = await axios.post('https://dqm-iw1g.onrender.com/upload/d', formData);
            setColumns(response.data);
            let len=columns.length;
            setType(new Array(len).fill(""));
            setMin(new Array(len).fill(0));
            setNewMin(new Array(len).fill(0));
            setMax(new Array(len).fill(0));
            setNewMax(new Array(len).fill(0));
            setList(new Array(len).fill(null).map(() => []));
            // setExists(new Array(len).fill(false));
            // setSelectedList(new Array(len).fill(null).map(() => []));
            setTotal(new Array(len).fill(0));
            setCount(new Array(len).fill(0));
            setInconsistency(new Array(len).fill(0));
        } catch (error) {
            console.error('Error uploading file:', error);
        }
    };

    const handleChange = async (index, value, x) => {
        try {
            let response;
            if (x === 3) {
                // setSelectedList(async prev => {
                //     const updated = [...prev];
                //     const current = Array.isArray(updated[index]) ? [...updated[index]] : [];
                //     exists[index] = current.includes(value);
                //     updated[index] = exists[index]
                //         ? current.filter(v => v !== value) // remove
                //         : [...current, value];
                //     return updated;
                // });// add
                const formdata = new FormData();
                formdata.append('index', index);
                formdata.append('value', value);
                // console.log(exists[index]);
                // formdata.append('exists', exists[index]);
                response = await axios.post('http://dqm-iw1g.onrender.com/calculate/d/list', formdata, {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    }
                });
                // setAverage(response.data[1]);
                // inconsistency[index] = (100 * (count[index] - response.data) / total[index]).toFixed(2);
                // console.log("Server response:", response.data); // or use it in state
                // console.log(value);
                // console.log(selectedList);
            } else {
                if (x === 1) newMin[index] = value;
                else if (x === 2) newMax[index] = value;
                console.log(newMin);
                const formData = new FormData();
                formData.append('min', newMin[index]);
                formData.append('max', newMax[index]);
                formData.append('index', index);
                // formData.append('value', value);
                // console.log(formData);
                response = await axios.post('http://dqm-iw1g.onrender.com/calculate/d/all', formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    }
                });
                // const data = response.data;
                // console.log(data);
                // if(inconsistency[index]!==0) c[1]-=inconsistency[index];
                // count[index] = data[0];
                // inconsistency[index] = data[1];
                // setAverage(data[2]);
                // c[1]+=inconsistency[index];
                // console.log('c[1]',c[1]);
                // console.log('c', c[0]);

            }
            console.log(response);
            console.log(response.data);
            count[index] = response.data[0];
            inconsistency[index] = response.data[1];
            setAverage(response.data[2]);
            console.log(response.data);
            setValues((preValues) => preValues.map((col, i) => i = index ? {
                ...col,
                count: count[index],
                inconsistency: inconsistency[index]
                // average: average
            } : col));
            console.log(values);
        } catch (error) {
            console.error('Error updating data:', error);
        }
    }

    // const handleSelect = (index, l) => {
    //     console.log(index, l);
    //     // selectedList[index].includes(l) ? selectedList[index].filter(i => i !== l) : selectedList[index].push(l);
    //     console.log(selectedList);
    //     setSelectedList(prev => {
    //         const updatedLists = [...prev];
    //         const currentList = updatedLists[index] || [];
    //         const item = l;
    //         if (currentList.includes(item)) {
    //             updatedLists[index] = currentList.filter(i => i !== item);
    //         } else if (currentList.length < 5) {
    //             updatedLists[index] = [...currentList, item];
    //         } else {
    //             updatedLists[index] = [currentList[1], currentList[2], currentList[3], currentList[4], item];
    //         }
    //         return updatedLists;
    //     });
    //     console.log(selectedList);
    // };
    // const handleSelect = (index, value) => {
        // setSelectedList(prev => {
        //     const updatedLists = [...prev];
        //     const currentList = updatedLists[index] ? [...updatedLists[index]] : [];
        //
        //     const item = list[index][itemIndex];
        //
        //     if (!item) return updatedLists; // Defensive guard
        //
        //     const itemPos = currentList.indexOf(item);
        //
        //     if (itemPos !== -1) {
        //         currentList.splice(itemPos, 1); // Remove item
        //     } else {
        //         if (currentList.length < 5) {
        //             currentList.push(item);
        //         } else {
        //             currentList.splice(0, 1); // Remove oldest
        //             currentList.push(item);
        //         }
        //     }
        //
        //     updatedLists[index] = currentList;
        //     return updatedLists;
        // })
    // const handleSelect = async (index, value) => {
        // selectedList.includes(value) ?
        //     (setSelectedList(prev => {
        //     const updated = [...prev];
        //     const row = (updated[index] || []).filter(item => item !== value);
        //     updated[index] = row;
        //     return updated;
        // }))
        // : (setSelectedList(prev => {
        //     const updated = [...prev];
        //     const row = new Set(updated[index] || []);
        //     row.add(value);
        //     updated[index] = Array.from(row); // to avoid duplicates
        //     return updated;
        // }));
    //     setSelectedList(prev => {
    //         const updated = [...prev];
    //         const exists = updated[index]?.includes(value);
    //         if (exists) {
    //             updated[index] = updated[index].filter(v => v !== value);
    //         } else {
    //             updated[index] = [...updated[index], values];
    //         }
    //         return updated;
    //     });
    //     console.log(selectedList);
    // };

    // const handleSelect = (index, value) => {
    //
    // };

    // useEffect(() => {
    //     console.log('SelectedList updated:', selectedList);
    // }, [selectedList]);
    // useEffect(() => {
    //     console.log('exists updated:', exists);
    // }, [exists]);

    const handleDelete = async (id) => {
        console.log(id);
        try {
            await axios.delete(`http://dqm-iw1g.onrender.com/delete/d/${id}`);
            axios.get('http://dqm-iw1g.onrender.com/all/d').then(response => {
                if (response && response.data) {
                    setDomains(response.data);
                }
            });
        } catch (error) {
            console.error('Error deleting all log entries:', error);
        }
    };

    const handleDeleteAll = async () => {
        try {
            await axios.delete('http://dqm-iw1g.onrender.com/delete/d/all');
            axios.get('http://dqm-iw1g.onrender.com/all/d').then(response => {
                if (response && response.data) {
                    setDomains(response.data);
                }
            });
        } catch (error) {
            console.error('Error deleting all log entries:', error);
        }
    };

    const saveDomain = async () => {
        try {
            await axios.post('http://dqm-iw1g.onrender.com/save/d');
            axios.get('http://dqm-iw1g.onrender.com/all/d').then(response => {
                if (response && response.data) {
                    setDomains(response.data);
                    console.log(response.data);
                }
            });
            console.log(domains);
        } catch (error) {
            console.log('Error saving rates:', error);
        }
    }


    // useEffect(() => {
    //     console.log('Values updated:', values);
    // }, [values]);

    // const handleCalculate = async () => {
    //     const formData = new FormData();
    //     formData.append('columns', JSON.stringify(selectedColumns));
    //     try {
    //         const response = await axios.post('http://dqm-iw1g.onrender.com/calculate/c', formData, {
    //             headers: {
    //                 'Content-Type': 'multipart/form-data',
    //             },
    //         });
    //         const data = response.data;
    //         console.log(data);
    //         // setComissionRate(data.comissionRate || 0.0);
    //         // setOmissionRate(data.omissionRate || 0.0);
    //     } catch (error) {
    //         console.error('Error calculating omission rates:', error);
    //     }
    // };

    return(
        <div style={{fontFamily: "'Roboto', sans-serif", backgroundColor: '#f5f5f5', minHeight: '100vh', padding: '20px', boxSizing: 'border-box',}}>
            <header style={{backgroundColor: '#4B2E83', padding: '15px 20px', color: 'white', display: 'flex', alignItems: 'center', justifyContent: 'space-between', boxShadow: '0 2px 5px rgba(0, 0, 0, 0.1)', borderRadius: '5px', marginBottom: '20px',}}>
                <h1 style={{flexGrow: 1, textAlign: 'center', margin: 0, marginLeft: '250px', fontSize: '24px', fontWeight: '500', color: 'white'}}>
                    Domain Check
                </h1>
                <a href={'http://dqm-iw1g.onrender.com/completeness'} style={{marginRight: '25px', color: "white", textDecoration: "none"}}>Completeness Check</a>
                <a href={'http://dqm-iw1g.onrender.com/format'} style={{color: "white", textDecoration: "none"}}>Format Check</a>
            </header>
            <div style={{backgroundColor: '#e6f0fa', padding: '20px', borderRadius: '8px', boxShadow: '0 2px 10px rgba(0, 0, 0, 0.05)', marginBottom: '20px', textAlign: 'center',}}>
                <h2 style={{fontSize: '20px', fontWeight: '500', color: '#333', marginBottom: '15px',}}>
                    Upload Excel File
                </h2>
                <input type="file" onChange={handleFileUpload} accept=".xlsx, .xls" style={{cursor: 'pointer', padding: '10px', border: '1px solid #ddd', borderRadius: '5px', fontSize: '16px', width: '300px', display: 'block', margin: '0 auto', color: '#555', backgroundColor: '#fafafa',}}/>
            </div>
            <div className="alert alert-primary" style={{ textAlign:'center'}}>
                <b>Definition: </b> Domain Consistency ensures that the attribute value fall within the predefined range or set of acceptable values.
                <br />
            </div>
                <br/>
                <div>
                    <table style={{width: '100%', margin: '0 auto', borderCollapse: 'collapse', backgroundColor: '#fff',}}>
                        <thead>
                        <tr>
                            <th style={{border: '1px solid #ddd', padding: '12px', backgroundColor: '#f0f0f0', fontWeight: '500', color: '#333',}}>Column Name</th>
                            <th style={{border: '1px solid #ddd', padding: '12px', textAlign: 'center', backgroundColor: '#f0f0f0', fontWeight: '500', color: '#333',}}>Data Type</th>
                            <th style={{border: '1px solid #ddd', padding: '12px', textAlign: 'center', backgroundColor: '#f0f0f0', fontWeight: '500', color: '#333',}}>Range in Data</th>
                            <th style={{border: '1px solid #ddd', padding: '12px', textAlign: 'center', backgroundColor: '#f0f0f0', fontWeight: '500', color: '#333',}}>Required Range</th>
                            <th style={{border: '1px solid #ddd', padding: '12px', textAlign: 'center', backgroundColor: '#f0f0f0', fontWeight: '500', color: '#333',}}>Total Attributes</th>
                            <th style={{border: '1px solid #ddd', padding: '12px', textAlign: 'center', backgroundColor: '#f0f0f0', fontWeight: '500', color: '#333',}}>inconsistency</th>
                            <th style={{border: '1px solid #ddd', padding: '12px', textAlign: 'center', backgroundColor: '#f0f0f0', fontWeight: '500', color: '#333',}}>inconsistency %</th>
                        </tr>
                        </thead>
                        <tbody>
                        {columns.map((col, index) =>
                            <tr key={index} style={{transition: 'background-color 0.2s',}}
                                onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#f9f9f9'}
                                onMouseLeave={(e) => e.currentTarget.style.backgroundColor = 'transparent'}>
                                <td style={{border: '1px solid #ddd', padding: '12px', color: '#555',}}>
                                    {col}
                                </td>
                                <td style={{border: '1px solid #ddd', padding: '12px', textAlign: 'center',}}>
                                    <select
                                        value={type[index]}
                                        onChange={(async e => {
                                            type[index] = e.target.value;
                                            const formData = new FormData();
                                            formData.append('col', col);
                                            formData.append('type', e.target.value);
                                            try {
                                                const response = await axios.post('http://dqm-iw1g.onrender.com/findrange', formData, {
                                                    headers: {
                                                        'Content-Type': 'multipart/form-data',
                                                    }
                                                });
                                                const data = response.data;
                                                console.log('data', data)
                                                console.log(type)
                                                // type[index]==="" ? c[0]-- : c[0]++;
                                                if (type[index] === "List") {
                                                    total[index] = parseInt(data[data.length - 4]);
                                                    count[index] = parseInt(data[data.length - 3]);
                                                    inconsistency[index] = parseFloat(data[data.length - 2]);
                                                    setAverage(parseFloat(data[data.length - 1]));
                                                    list[index] = data.slice(0, -4);
                                                    // if(inconsistency[index]!==0) c[1]-=inconsistency[index];
                                                    // inconsistency[index] = (100 * count[index] / total[index]).toFixed(2);
                                                    // c[1]+=inconsistency[index]
                                                    // console.log(c[1]);
                                                    // console.log(c);
                                                    console.log(inconsistency[index]);
                                                    console.log(inconsistency);
                                                    setValues((preValues) => preValues.map((col, i) => i = index ? {
                                                        ...col,
                                                        total: total,
                                                        count: count,
                                                        inconsistency: inconsistency
                                                        // average: average
                                                    } : col));
                                                }
                                                else {
                                                    min[index] = data[0];
                                                    newMin[index] = data[0];
                                                    max[index] = data[1];
                                                    newMax[index] = data[1];
                                                    total[index] = data[2];
                                                    count[index] = data[3];
                                                    inconsistency[index] = data[4];
                                                    setAverage(data[5]);
                                                    // if(inconsistency[index]!==0) c[1]-=inconsistency[index];
                                                    // inconsistency[index] = (100 * count[index] / total[index]).toFixed(2);
                                                    // c[1]+=inconsistency[index];
                                                    // console.log(c[1]);
                                                    // console.log(c);
                                                    setValues((preValues) => preValues.map((col, i) => i = index ? {
                                                        ...col,
                                                        min: min[index],
                                                        max: max[index],
                                                        total: total,
                                                        count: count,
                                                        inconsistency: inconsistency[index],
                                                        // average: average
                                                    } : col));
                                                }
                                            } catch (error) {console.error('Error sending data:', error);}
                                        })}
                                        style={{padding: "10px", backgroundColor: "#f7f7f7", color: "#333", border: "1px solid #ccc", borderRadius: "5px", cursor: "pointer", fontSize: "16px", outline: "none",}}
                                    >
                                        {/* correct here for the zone and the code option for the railway zone and railway code */}
                                        <option value="">Select the option</option>
                                        <option value="Integer">Integer</option>
                                        <option value="Decimal">Decimal</option>
                                        <option value="List">List</option>
                                        <option value="String">String</option>
                                    </select>
                                </td>
                                {/*{list[index]!=="" && (min[index] ===10000 || max[index] ===-10000) ? (<td style={{border: '1px solid #ddd', padding: '12px', borderCollapse: "collapse", textAlign: 'left'}}></td>) : */}
                                {
                                    // (type[index] === "" && <td style={{border: '1px solid #ddd', padding: '12px', textAlign: 'center'}}> </td>) ||
                                    (type[index] === 'List' && <td style={{border: '1px solid #ddd', padding: '12px', width: '275px', textAlign: 'left', verticalAlign: 'top'}}>{list[index].map((l, i) => (<div key={i}>{l}</div>
                                    ))}</td>) ||
                                    ((min[index]!==10000 && max!==-10000) && <td style={{border: '1px solid #ddd', padding: '12px', textAlign: 'center'}}>min: {min[index]} max: {max[index]}</td>) ||
                                    ((min[index]===10000 || max===-10000) && <td style={{border: '1px solid #ddd', padding: '12px', textAlign: 'center'}}> </td>)
                                }
                                {type[index] !== "" && (min[index] ===10000 || max[index] ===-10000)
                                    ? <td style={{border: '1px solid #ddd', padding: '12px', textAlign: 'left'}}></td>
                                    : (type[index]==='List'
                                            ? <td style={{ border: '1px solid #ddd', padding: '12px', width: '275px', textAlign: 'left', verticalAlign: 'middle' }}>
                                            {list[index].map((l, i) => (
                                                <div key={i} className="checkbox-row">
                                                    <input
                                                        type="checkbox"
                                                        id={`list-${index}-${i}`}
                                                        // checked={selectedList[index]?.includes(l)}
                                                        onClick={() => handleChange(index, l, 3)}
                                                        className="checkbox"
                                                    />
                                                    <label htmlFor={`list-${index}-${i}`} className="label">
                                                        {l}
                                                    </label>
                                                </div>
                                            ))}
                                            </td>
                                            : <td style={{border: '1px solid #ddd', padding: '12px', textAlign: 'center', width:'100px' }}>
                                            <label>Min:</label><input type="number" defaultValue={min[index]} onChange={(e) => handleChange(index, parseFloat(e.target.value), 1)} style={{ width: '50%', textAlign: 'center' }}/>
                                                <br/>
                                            <label>Max:</label><input type="number" defaultValue={max[index]} onChange={(e) => handleChange(index, parseInt(e.target.value), 2)} style={{ width: '50%', textAlign: 'center' }}/>
                                            </td>
                                )}
                                {type[index]!=="" &&
                                    <td style={{border: '1px solid #ddd', padding: '12px', textAlign: 'center',}}>{total[index]}</td> }
                                {type[index]!=="" &&
                                    <td style={{border: '1px solid #ddd', padding: '12px', textAlign: 'center',}}>{count[index]}</td>
                                }
                                {type[index]!=="" &&
                                    <td style={{border: '1px solid #ddd', padding: '12px', textAlign: 'center',}}>{inconsistency[index]}</td>
                                }
                            </tr>
                        )}
                        </tbody>
                    </table>
                </div>
            <br/>
            <br/>
                {type && <div style={{justifyContent: "center", gap: '100px',textAlign: 'center', alignItems: 'center', display:'flex'}}>
                    {/*<button*/}
                    {/*    onClick={setB(true)}*/}
                    {/*    style={{padding: '10px 20px', backgroundColor: '#28a745', color: 'white', border: 'none', borderRadius: '5px', fontSize: '16px', cursor: 'pointer', transition: 'background-color 0.3s',}}*/}
                    {/*    onMouseEnter={(e) => e.target.style.backgroundColor = '#218838'}*/}
                    {/*    onMouseLeave={(e) => e.target.style.backgroundColor = '#28a745'}*/}
                    {/*>*/}
                    {/*    Calculate Domain Consistency*/}
                    {/*</button>*/}
                    <button
                        onClick={saveDomain}
                        style={{padding: '10px 20px', backgroundColor: '#28a745', color: 'white', border: 'none', borderRadius: '5px', fontSize: '16px', cursor: 'pointer', transition: 'background-color 0.3s',}}
                        onMouseEnter={(e) => e.target.style.backgroundColor = '#218838'}
                        onMouseLeave={(e) => e.target.style.backgroundColor = '#28a745'}
                    >
                        Save
                    </button>
                    Domain Consistency = {average}
                </div>}
            <br/>
            <br/>
            <br/>
            <div style={{borderRadius: '8px',boxShadow: '0 2px 10px rgba(0, 0, 0, 0.05)',}}>
                <table id="myTable" style={{width: '100%', borderCollapse: 'collapse', backgroundColor: '#fff'}}>
                    <thead>
                    <tr>
                        {/*<th style={{border: '1px solid #ddd', padding: '12px', backgroundColor: '#f0f0f0', fontWeight: '500', color: '#333'}}>ID</th>*/}
                        <th style={{border: '1px solid #ddd', padding: '12px', backgroundColor: '#f0f0f0', fontWeight: '500', color: '#333'}}>Filename</th>
                        <th style={{border: '1px solid #ddd', padding: '12px', backgroundColor: '#f0f0f0', fontWeight: '500', color: '#333'}}>Attribute</th>
                        <th style={{border: '1px solid #ddd', padding: '12px', backgroundColor: '#f0f0f0', fontWeight: '500', color: '#333'}}>Type</th>
                        <th style={{border: '1px solid #ddd', padding: '12px', backgroundColor: '#f0f0f0', fontWeight: '500', color: '#333'}}>Min</th>
                        <th style={{border: '1px solid #ddd', padding: '12px', backgroundColor: '#f0f0f0', fontWeight: '500', color: '#333'}}>Max</th>
                        <th style={{border: '1px solid #ddd', padding: '12px', backgroundColor: '#f0f0f0', fontWeight: '500', color: '#333'}}>Inconsistency</th>
                        <th style={{border: '1px solid #ddd', padding: '12px', backgroundColor: '#f0f0f0', fontWeight: '500', color: '#333'}}>Average</th>
                        <th style={{border: '1px solid #ddd', padding: '12px', backgroundColor: '#f0f0f0', fontWeight: '500', color: '#333'}}>Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    {domains.length > 0 ? (
                        domains.map(domain =>
                        <tr key={domain.id} style={{transition: 'background-color 0.2s'}}
                        onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#f9f9f9'}
                        onMouseLeave={(e) => e.currentTarget.style.backgroundColor = 'transparent'}>
                        {/*<td style={{border: '1px solid #ddd', padding: '12px', color: '#555',}}>{domain.id || "N/A"}</td>*/}
                        <td style={{border: '1px solid #ddd', padding: '12px', color: '#555',}}>{domain.fileName || "N/A"}</td>
                        <td style={{border: '1px solid #ddd', padding: '12px', color: '#555',}}>{domain.attribute || "N/A"}</td>
                        <td style={{border: '1px solid #ddd', padding: '12px', color: '#555',}}>{domain.type || "N/A"}</td>
                        <td style={{border: '1px solid #ddd', padding: '12px', color: '#555',}}>{domain.min || "N/A"}</td>
                        <td style={{border: '1px solid #ddd', padding: '12px', color: '#555',}}>{domain.max || "N/A"}</td>
                        <td style={{border: '1px solid #ddd', padding: '12px', color: '#555',}}>{domain.inconsistency || "N/A"}</td>
                        <td style={{border: '1px solid #ddd', padding: '12px', color: '#555',}}>{domain.average || "N/A"}</td>
                        <td style={{border: '1px solid #ddd', padding: '12px', textAlign: 'center'}}>
                                <button
                                    onClick={() => handleDelete(domain.id)}
                                    style={{padding: '8px 12px', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: '5px', fontSize: '14px', cursor: 'pointer', transition: 'background-color 0.3s'}}
                                    onMouseEnter={(e) => e.target.style.backgroundColor = '#c82333'}
                                    onMouseLeave={(e) => e.target.style.backgroundColor = '#dc3545'}
                                >
                                    Delete
                                </button>
                        </td>
                        </tr>
                        )): (
                        <tr>
                            <td colSpan="9" style={{padding: '20px', textAlign: 'center', color: '#777', fontStyle: 'italic', itemAlign: 'center'}}>
                                No issues found.
                            </td>
                        </tr>
                    )}
                    </tbody>
                </table>
                <div style={{ textAlign: 'right', marginTop: '15px' }}>
                    <button
                        onClick={() => handleDeleteAll()}
                        style={{padding: '8px 15px', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: '5px', fontSize: '14px', cursor: 'pointer', transition: 'background-color 0.3s'}}
                        onMouseEnter={(e) => e.target.style.backgroundColor = '#c82333'}
                        onMouseLeave={(e) => e.target.style.backgroundColor = '#dc3545'}
                    >
                        Delete All
                    </button>
                </div>
            </div>
        </div>);
}
export default DomainCheck;
