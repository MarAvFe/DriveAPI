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
		}else if(this.props.node && this.props.node.isDir){
			currentViewer = (<div style={style.base}>
				<div>Size: {this.props.node.size}</div>
			</div>);
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
        this.handleInputChange = this.handleInputChange.bind(this);
        this.crefil = this.crefil.bind(this);
        this.credir = this.credir.bind(this);
        this.edit = this.edit.bind(this);
        this.delet = this.delet.bind(this);
        this.share = this.share.bind(this);
        this.mov = this.mov.bind(this);
        this.copy = this.copy.bind(this);

		xDrive.ls("my@mail.com").then((response) => {
			data = response.data;
			data.toggled = true;
			DataMiddleware.json(data);
			this.forceUpdate();
			console.log(data);
		});

    }
    onToggle(node, toggled){
		if(this.state.cursor){this.state.cursor.active = false};
        node.active = true;
		this.setState({ cursor: node });
        if(node.children){ node.toggled = toggled; }
    }
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;

        this.setState({
          [name]: value
        });
      }
    crefil(){console.log("crefil:" + this.state.cre1 + ", " + this.state.cre2)}
    credir(){console.log("credir:" + this.state.dir1)}
    edit(){console.log("edit:" + this.state.edit)}
    delet(){console.log("delete:" + this.state.delet)}
    share(){console.log("share:" + this.state.share)}
    mov(){console.log("move:" + this.state.mov)}
    copy(){console.log("copy:" + this.state.copy)}
    render(){
        return (
			<StyleRoot>
            <div style={styles.component}>
            <ul>
            <li style={styles.actions} onClick={this.crefil}>Crear archivo (nombre | contenido)</li>
                <input name="cre1" onChange={this.handleInputChange}></input>
                <input name="cre2" onChange={this.handleInputChange}></input>
            <li style={styles.actions} onClick={this.credir}>Crear directorio (nombre)</li>
                <input name="dir1" onChange={this.handleInputChange}></input>
            <li style={styles.actions} onClick={this.edit}>Editar (Nuevo nombre)</li>
                <input name="edit" onChange={this.handleInputChange}></input>
            <li style={styles.actions} onClick={this.delet}>Eliminar</li>
            <li style={styles.actions} onClick={this.share}>Compartir (Email Destinatario)</li>
                <input name="share" onChange={this.handleInputChange}></input>
            <li style={styles.actions} onClick={this.mov}>Mover (Nueva Ruta)</li>
                <input name="mov" onChange={this.handleInputChange}></input>
            <li style={styles.actions} onClick={this.copy}>Copiar (Nueva Ruta)</li>
                <input name="copy" onChange={this.handleInputChange}></input>
            </ul>
            </div>
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
