'use strict';

import React from 'react';
import ReactDOM from 'react-dom';
import PropTypes from 'prop-types';
import styles from './styles';
import {StyleRoot} from 'radium';
import {Treebeard, decorators} from 'react-treebeard';

import xDrive from './services';


// Example: Customising The Header Decorator To Include Icons
decorators.Header = ({style, node}) => {
    const iconType = node.isDir ? 'folder' : 'file-text';
    const iconClass = `fa fa-${iconType}`;
    const iconStyle = {marginRight: '5px'};

    return (
        <div style={style.base}>
            <div style={style.title}>
                <i className={iconClass} style={iconStyle}/>
                {node.name}
            </div>
        </div>
    );
};

class DataMiddleware extends React.Component{
	static addParents(node){
		if(node.content){return};
		for(let i = 0; i< node.children.length; i++){
			node.children[i].parent = node.parent ? node.parent + node.name + "/": node.name + "/";
			if (node.children[i].children && node.children[i].isDir) {this.addParents(node.children[i])};
		}
	}
	static json(data){
		this.addParents(data);
	}
}

var currentViewer = null;
class NodeViewer extends React.Component {
	render() {
        const style = styles.viewer;
		if(this.props.node && this.props.node.content){
			currentViewer = (<div style={style.base}>
				<div>Size: {this.props.node.size}</div>
				<div>Creation date: {this.props.node.creationDate}</div>
				<div>Modification date: {this.props.node.modificationDate}</div>
				<div>Content: {this.props.node.content}</div>
			</div>);
			return currentViewer;
		}
		return currentViewer;
        //let location = this.props.node && this.props.node.parent  ? this.props.node.parent + this.props.node.name : "";
    }
}
NodeViewer.propTypes = {
    node: PropTypes.object
};
var data = {
    name: '/',
    toggled: true,
    content: [],
	loading: true
}

class TreeExample extends React.Component {
    constructor(props){
        super(props);
        this.state = {};
        this.onToggle = this.onToggle.bind(this);

		xDrive.ls("my@mail.com").then((response) => {
			data = response.data;
			data.toggled = true;
			DataMiddleware.json(data);
			this.forceUpdate();
			console.log(data);
		});

    }
    onToggle(node, toggled){
        if(!node.isDirectory){
			if(this.state.cursor){this.state.cursor.active = false};
        	node.active = true;
			this.setState({ cursor: node });
		}
        if(node.children){ node.toggled = toggled; }
    }
    render(){
        return (
			<StyleRoot>
			<div style={styles.component}>
            	<Treebeard
                	data={data}
					decorators={decorators}
                	onToggle={this.onToggle}
            	/>
			</div>
			<div style={styles.component}>
            	<NodeViewer node={this.state.cursor}/>
			</div>
			</StyleRoot>
        );
    }
}

export default TreeExample;
