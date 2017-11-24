'use strict';

import React from 'react';
import ReactDOM from 'react-dom';
import PropTypes from 'prop-types';
import styles from './styles';
import {StyleRoot} from 'radium';
import {Treebeard, decorators} from 'react-treebeard';

import xDrive from './services';


// Test data
const data = {
    name: 'root',
    toggled: true,
    children: [
        {
            name: 'parent',
            children: [
                { name: 'child1' },
                { name: 'child2' }
            ]
        },
        {
            name: 'loading parent',
            loading: true,
            children: []
        },
        {
            name: 'parent',
            children: [
                {
                    name: 'nested parent',
                    children: [
                        { name: 'nested child 1' },
                        { name: 'nested child 2' }
                    ]
                }
            ]
        }
    ]
};

// Example: Customising The Header Decorator To Include Icons
decorators.Header = ({style, node}) => {
    const iconType = node.children ? 'folder' : 'file-text';
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
		for(let i = 0; i< node.children.length; i++){
			node.children[i].parent = node.parent ? node.parent + node.name + "/": node.name + "/";
			if (node.children[i].children) {this.addParents(node.children[i])};
		}
	}
	static json(data){
		this.addParents(data);
	}
}

class NodeViewer extends React.Component {
	render() {
		xDrive.ls();
		DataMiddleware.json(data);
        const style = styles.viewer;
        let content = this.props.node ? this.props.node.parent + this.props.node.name : "";
        return <div>{content}</div>;
    }
}
NodeViewer.propTypes = {
    node: PropTypes.object
};

class TreeExample extends React.Component {
    constructor(props){
        super(props);
        this.state = {};
        this.onToggle = this.onToggle.bind(this);
    }
    onToggle(node, toggled){
		console.log(node);
		console.log(toggled);
        if(this.state.cursor){this.state.cursor.active = false;}
        node.active = true;
        if(node.children){ node.toggled = toggled; }
        this.setState({ cursor: node });
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
