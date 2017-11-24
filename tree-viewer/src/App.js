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
		console.log("dayumDaniel");
		console.log(this.viewerDialog);
        const style = styles.viewer;
		if(this.props.priority){
			currentViewer = this.props.dialog;
		}else if(this.props.node && this.props.node.content){
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
		}else{
			currentViewer = null;
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

const mail = "my@mail.com";

class TreeExample extends React.Component {
    constructor(props){
        super(props);
        this.state = {};
        this.onToggle = this.onToggle.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
        this.refresh = this.refresh.bind(this);
        this.crefil = this.crefil.bind(this);
        this.credir = this.credir.bind(this);
        this.edit = this.edit.bind(this);
        this.delet = this.delet.bind(this);
        this.share = this.share.bind(this);
        this.mov = this.mov.bind(this);
        this.requestMove = this.requestMove.bind(this);
        this.requestTouch = this.requestTouch.bind(this);
        this.cancel = this.cancel.bind(this);
        this.copy = this.copy.bind(this);

		this.refresh();
    }
	refresh(){
		xDrive.ls(mail).then((response) => {
			data = response.data;
			data.toggled = true;
			DataMiddleware.json(data);
			this.forceUpdate();
			console.log(data);
		});
	xDrive.cd(mail, "/");

	}
    onToggle(node, toggled){
		if(this.state.cursor){this.state.cursor.active = false};
        node.active = true;
		this.setState({ cursor: node });
		xDrive.cd(mail, node.isDir ? node.parent + node.name: node.parent);
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
    crefil(){
		let viewerDialog = (
			<div>
				<div>Nombre del archivo</div>
				<input value={this.state.inputValue1} type="text" class="form-control" onChange={evt => this.updateInputValue1(evt)}/>
				<div>Contenido del archivo</div>
				<input value={this.state.inputValue2} type="text" class="form-control" onChange={evt => this.updateInputValue2(evt)}/>
				<button type="button" class="btn" onClick={this.requestTouch}>Crear</button>
				<button type="button" class="btn" onClick={this.cancel}>Cancelar</button>
			</div>
			);
			this.setState({ priority: true, dialog: viewerDialog });
	}
	requestTouch(){
		xDrive.touch(mail, this.state.inputValue1, this.state.inputValue2).then(() => this.refresh());
	}
	updateInputValue1(evt) {
    	this.setState({
      		inputValue1: evt.target.value
    	});
  	}
	updateInputValue2(evt) {
    	this.setState({
      		inputValue2: evt.target.value
    	});
  	}
    credir(){console.log("credir:" + this.state.dir1)}
    edit(){console.log("edit:" + this.state.edit)}
    delet(){console.log("delete:" + this.state.delet)}
    share(){console.log("share:" + this.state.share)}
    mov(){
		let viewerDialog = (
			<div>
				<div>Seleccione la carpeta de destino</div>
				<button type="button" class="btn" onClick={this.requestMove}>Mover</button>
				<button type="button" class="btn" onClick={this.cancel}>Cancelar</button>
			</div>
		);
		this.setState({ priority: true, dialog: viewerDialog });
	}
	requestMove(){

	}
    copy(){console.log("copy:" + this.state.copy);}
	cancel(){
		this.setState({ priority: false});
	}
    render(){
        return (
			<StyleRoot>
            <div style={styles.component}>
            <ul>
            <li style={styles.actions} onClick={this.crefil}>Crear archivo</li>
            <li style={styles.actions} onClick={this.credir}>Crear directorio </li>
            <li style={styles.actions} onClick={this.edit}>Editar</li>
            <li style={styles.actions} onClick={this.delet}>Eliminar</li>
            <li style={styles.actions} onClick={this.share}>Compartir</li>
            <li style={styles.actions} onClick={this.mov}>Mover</li>
            <li style={styles.actions} onClick={this.copy}>Copiar</li>
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
            	<NodeViewer node={this.state.cursor} priority={this.state.priority} dialog={this.state.dialog}/>
			</div>
			</StyleRoot>
        );
    }
}

export default TreeExample;
