import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import TreeExample from './App';
import registerServiceWorker from './registerServiceWorker';

ReactDOM.render(<TreeExample/>, document.getElementById('content'));
registerServiceWorker();
